package com.cloud.springredis;

import com.cloud.springredis.service.SecKill;
import com.cloud.springredis.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootApplication
public class SpringredisApplication implements CommandLineRunner {
    @Autowired
    StringRedisTemplate template;
    public static void main(String[] args) {
        SpringApplication.run(SpringredisApplication.class, args);


    }

    @Override
    public void run(String... args) throws Exception {
        template.convertAndSend("shop","jie'r is my baby");
        Service service = new Service();
        for(int i=0;i<100;i++){
            SecKill secKill = new SecKill(service);
            secKill.start();
        }
    }
}
