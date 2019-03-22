package com.cloud.springredis.redis;

import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Collections;


public class RedisUtil {

    private static JedisPool jedisPool;
    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    private static final Long RELEASE_SUCCESS = 1L;
    private static void initPool(){
        String host = "127.0.0.1";
        int port = 6379;
        try{
            JedisPoolConfig config = new JedisPoolConfig();
            //最大连接数，如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
            config.setMaxTotal(200);
            //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
            config.setMaxWaitMillis(15*1000);
            //最大空闲数，控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
            config.setMaxIdle(20);
            //最小空闲数
            config.setMinIdle(8);
            //是否在从池中取出连接前进行检验，如果检验失败，则从池中去除连接并尝试取出另一个
            config.setTestOnBorrow(true);
            //在return给pool时，是否提前进行validate操作
            config.setTestOnReturn(true);
            jedisPool = new JedisPool(config,host,port,10*100);
        }catch (Exception e){
            if(jedisPool != null){
                jedisPool.close();
            }
        }
    }
    static {
        initPool();
    }
    private static synchronized void poolInit(){
        if(jedisPool == null){
            initPool();
        }
    }
    //用于多线程环境同步初始化
    public static Jedis getJedis(){
        if(jedisPool == null){
            poolInit();
        }
        Jedis jedis = null;
        try{
            if(jedis == null){
                jedis = jedisPool.getResource();
            }
        }catch (Exception e){
            if(jedis != null && jedisPool != null){
                jedis.close();
            }
        }
        return jedis;
    }
    public static boolean lock(String key,String value,int expire){
        Jedis jedis = getJedis();
        if(jedis == null){
            return false;
        }
        String result = null;
        //保证使上锁和设置过期时间为一个原子操作
        result = jedis.set(key,value,SET_IF_NOT_EXIST,SET_WITH_EXPIRE_TIME,expire);
        if(LOCK_SUCCESS.equals(result)){
            return true;
        }
        return false;
    }
    /*
     key为锁标志  request为拥有该锁的客户端的标志 就是上锁的传入的value  这样能保证 只有拥有该锁的客户端才能释放该锁   其他客户端或者线程不能释放该锁
     */
    public static boolean releaseLock(String key,String requestId){
        Jedis jedis = getJedis();
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = jedis.eval(script, Collections.singletonList(key), Collections.singletonList(requestId));
        if(RELEASE_SUCCESS.equals(result)){
            return true;
        }
        else{
            return false;
        }
    }
}
