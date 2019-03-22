package com.cloud.springredis.service;

import com.cloud.springredis.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

//@Component
public class Service {
     private StringRedisTemplate template;
    int n = 500;
    public void seckill(){
        RedisUtil.lock("resource",Thread.currentThread().getName().toString(),5000);
        //template.getConnectionFactory().getConnection().set("resource".getBytes(),Thread.currentThread().getName().toString().getBytes(),"NX","PX",5000);
        System.out.println(Thread.currentThread().getName() + "获得了锁");
        System.out.println(--n);
        RedisUtil.releaseLock("resource",Thread.currentThread().getName().toString());
    }
}
