package com.cloud.springredis.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Receiver {
    private static final Logger logger = LoggerFactory.getLogger(Receiver.class);
    public void receiveMessage(String message){
        System.out.println(message);
        logger.info(message);
    }
}
