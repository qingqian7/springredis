package com.cloud.springredis.service;

public class SecKill extends Thread {
    private Service service;
    public SecKill(Service service){
        this.service = service;
    }

    @Override
    public void run() {
        service.seckill();
    }
}
