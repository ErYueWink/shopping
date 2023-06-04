package com.itbaizhan.shopping_seckill_service.redis;

import com.itbaizhan.shopping_common.pojo.Orders;
import com.itbaizhan.shopping_common.pojo.SeckillGoods;
import com.itbaizhan.shopping_common.service.SeckillGoodsService;
import com.itbaizhan.shopping_common.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

/**
 * 监听过期订单
 */
@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SeckillService seckillService;

    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        // 过期订单id
        String orderId = message.toString();
        Orders orders = (Orders) redisTemplate.opsForValue().get(orderId + "_copy"); // 获取订单副本数据
        Long goodId = orders.getCartGoods().get(0).getGoodId();
        Integer num = orders.getCartGoods().get(0).getNum();
        // 查询秒杀商品
        SeckillGoods seckillGoods = seckillService.findSeckillGoodsByRedis(goodId);
        // 回退库存
        seckillGoods.setStockCount(seckillGoods.getStockCount()+num);
        redisTemplate.boundHashOps("seckillGoods").put(goodId,seckillGoods);
        // 删除复制订单数据
        redisTemplate.delete(orderId+"_copy");
    }
}
