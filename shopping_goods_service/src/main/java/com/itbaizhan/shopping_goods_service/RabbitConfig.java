package com.itbaizhan.shopping_goods_service;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // 交换机
    private final String GOODS_EXCHANGE = "goods_exchange";
    // 同步消息队列
    private final String SYNC_GOODS_QUEUE = "sync_goods_queue";
    // 删除消息队列
    private final String DEL_GOODS_QUEUE = "del_goods_queue";
    // 同步购物车消息队列
    private final String SYNC_CART_QUEUE = "sync_cart_queue";
    // 删除购物车中商品消息队列
    private final String DEL_CART_QUEUE = "del_cart_queue";
    @Bean(GOODS_EXCHANGE)
    public Exchange getExchange (){
        return ExchangeBuilder.topicExchange(GOODS_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean(SYNC_GOODS_QUEUE)
    public Queue getQueue1(){
        return new Queue(SYNC_GOODS_QUEUE);
    }

    @Bean(DEL_GOODS_QUEUE)
    public Queue getQueue2(){
        return new Queue(DEL_GOODS_QUEUE);
    }

    @Bean(SYNC_CART_QUEUE)
    public Queue getQueue3(){
        return new Queue(SYNC_CART_QUEUE);
    }

    @Bean(DEL_CART_QUEUE)
    public Queue getQueue4(){
        return new Queue(DEL_CART_QUEUE);
    }

    @Bean
    public Binding exchangeToQueue1(@Qualifier(GOODS_EXCHANGE) Exchange exchange,
                                    @Qualifier(SYNC_GOODS_QUEUE) Queue queue){
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("#.sync_goods.#")
                .noargs();
    }
    @Bean
    public Binding exchangeToQueue(@Qualifier(GOODS_EXCHANGE) Exchange exchange,
                                   @Qualifier(DEL_GOODS_QUEUE) Queue queue){
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("#.del_goods.#")
                .noargs();
    }

    @Bean
    public Binding exchangeToQueue3(@Qualifier(GOODS_EXCHANGE) Exchange exchange,
                                    @Qualifier(SYNC_CART_QUEUE) Queue queue){
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("#.sync_cart.#")
                .noargs();
    }
    @Bean
    public Binding exchangeToQueue4(@Qualifier(GOODS_EXCHANGE) Exchange exchange,
                                   @Qualifier(DEL_CART_QUEUE) Queue queue){
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("#.del_cart.#")
                .noargs();
    }


}
