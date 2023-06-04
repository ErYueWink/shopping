package com.itbaizhan.shopping_seckill_service.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itbaizhan.shopping_common.exception.BusException;
import com.itbaizhan.shopping_common.pojo.CartGoods;
import com.itbaizhan.shopping_common.pojo.Orders;
import com.itbaizhan.shopping_common.pojo.SeckillGoods;
import com.itbaizhan.shopping_common.result.CodeEnum;
import com.itbaizhan.shopping_common.service.SeckillService;
import com.itbaizhan.shopping_seckill_service.mapper.SeckillGoodsMapper;
import com.mysql.cj.util.TimeUtil;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@DubboService
@Component
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    /**
     * 每分钟将mysql中的秒杀商品数据同步到redis中
     * 条件为startTime < now < endTime,stockCount > 0
     */
    @Scheduled(cron = "0/5 * * * * *")
    public void refreshRedis(){
        // 将redis中的商品库存数同步到mysql中
        List<SeckillGoods> seckillGoodsList = redisTemplate.boundHashOps("seckillGoods").values();
        for (SeckillGoods seckillGoods : seckillGoodsList) {
            SeckillGoods goods = seckillGoodsMapper.selectById(seckillGoods.getId());
            goods.setStockCount(seckillGoods.getStockCount());
            seckillGoodsMapper.updateById(goods);
        }
        System.out.println("将mysql中的数据同步到redis中");
        QueryWrapper<SeckillGoods> queryWrapper = new QueryWrapper();
        Date date = new Date();
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        queryWrapper.le("startTime",now)
                .ge("endTime",now)
                .gt("stockCount",0);
        // 查询出正在秒杀的商品
        List<SeckillGoods> seckillGoods = seckillGoodsMapper.selectList(queryWrapper);
        // 删除redis中的秒杀商品数据
        redisTemplate.delete("seckillGoods");
        // 将数据同步到redis中
        for (SeckillGoods seckillGood : seckillGoods) {
            redisTemplate.boundHashOps("seckillGoods").put(seckillGood.getGoodsId(),seckillGood);
        }
    }

    @Override
    public Page<SeckillGoods> findPageByRedis(int page, int size) {
        // 获取所有正在秒杀的商品
        List<SeckillGoods> seckillGoodsList = redisTemplate.boundHashOps("seckillGoods").values();
         // 构造分页查询条件
        int start = (page - 1) * size; // 开始截取的位置
        // 结束截取的位置
        int end = start + size > seckillGoodsList.size() ? seckillGoodsList.size() : start + size;
        // 每页秒杀商品结果集
        List<SeckillGoods> seckillGoods = seckillGoodsList.subList(start, end);
        // 封装为MP的Page对象
        Page<SeckillGoods> page1 = new Page();
        page1.setCurrent(page) // 当前页
                .setSize(size) // 每页条数
                .setTotal(seckillGoodsList.size()) // 总条数
                .setRecords(seckillGoods); // 结果集
        return page1;
    }

    @Override
    public SeckillGoods findSeckillGoodsByRedis(Long goodsId) {
        SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(goodsId);
        return seckillGoods;
    }

    @Override
    public Orders createOrder(Orders orders) {
        //手动生成订单
        orders.setId(IdWorker.getIdStr()); // 使用雪花算法生成id
        orders.setCreateTime(new Date()); // 订单创建时间
        orders.setExpire(new Date(new Date().getTime()+1000*60*5)); // 设置订单过期时间为5分钟
        // 计算订单价格
        CartGoods cartGoods = orders.getCartGoods().get(0);
        Integer num = cartGoods.getNum();
        BigDecimal price = cartGoods.getPrice();
        BigDecimal sum = price.multiply(BigDecimal.valueOf(num));
        orders.setPayment(sum);

        SeckillGoods seckillGoods = findSeckillGoodsByRedis(cartGoods.getGoodId());
        Integer stockCount = seckillGoods.getStockCount();
        if(stockCount <= 0){ // 如果库存小于等于0抛出异常
            throw new BusException(CodeEnum.STOCK_COUNT_ERROR);
        }
        // 减少库存
        seckillGoods.setStockCount(stockCount - num);
        redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getGoodsId(),seckillGoods);

        // 将订单数据保存到redis中
        // 设置键的序列化方式为string类型
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // 给订单设置过期时间
        redisTemplate.opsForValue().set(orders.getId(),orders,1, TimeUnit.MINUTES);
        /**
         * 给订单创建副本副本的过期时间长于原订单
         * redis过期触发过期事件时，redis只能拿到key拿不到value
         * 订单过期后需要回退商品库存，必须拿到value即订单详情，进行回退操作
         * 保存一个订单副本，过期时间长于原订单，此时我们就可以通过副本来拿到订单详情
         */
        redisTemplate.opsForValue().set(orders.getId()+"_copy",orders,2,TimeUnit.MINUTES);
        return orders;
    }

    @Override
    public Orders findOrder(String id) {
        Orders orders = (Orders) redisTemplate.opsForValue().get(id);
        return orders;
    }

    @Override
    public Orders pay(String orderId) {
        // 查询订单设置订单数据
        Orders orders = (Orders) redisTemplate.opsForValue().get(orderId);
        if(orders == null){
            throw new BusException(CodeEnum.ORDER_PAY_ERROR);
        }
        orders.setPaymentTime(new Date());
        orders.setPaymentType(2);
        orders.setStatus(2);
        // 从redis中删除订单数据
        redisTemplate.delete(orderId);
        // 删除复制订单数据
        redisTemplate.delete(orderId+"_copy");
        return orders;
    }
}
