package net.traslated.redis;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisManagerFactory {
    private RedisManagerFactory() {
    }

    public static RedisManager createManager(String jedisHost, int jedisPort) {
        return new RedisManager(new JedisPool(jedisHost, jedisPort));
    }

    public static RedisManager createManager(String jedisHost, int jedisPort, JedisPoolConfig jedisPoolConfig) {
        return new RedisManager(new JedisPool(jedisPoolConfig, jedisHost, jedisPort));
    }
}
