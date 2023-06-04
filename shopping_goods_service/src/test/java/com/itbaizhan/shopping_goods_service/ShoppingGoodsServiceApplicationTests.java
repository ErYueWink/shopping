package com.itbaizhan.shopping_goods_service;

import com.itbaizhan.shopping_common.pojo.GoodsDesc;
import com.itbaizhan.shopping_common.service.GoodsService;
import com.itbaizhan.shopping_common.service.SearchService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ShoppingGoodsServiceApplicationTests {

//    @Autowired
//    private GoodsService goodsService;
//    @DubboReference
//    private SearchService searchService;
//    @Test
//    void contextLoads() {
//        List<GoodsDesc> all = goodsService.findAll();
//        System.out.println(all);
//    }

//     将数据同步到ES中
//    @Test
//    public void syncToEs(){
//        List<GoodsDesc> goodsDescs = goodsService.findAll();
//        for (GoodsDesc goodsDesc : goodsDescs) {
//            if(goodsDesc.getIsMarketable()){
//                searchService.syncGoodsToEs(goodsDesc);
//            }
//        }
//    }


}
