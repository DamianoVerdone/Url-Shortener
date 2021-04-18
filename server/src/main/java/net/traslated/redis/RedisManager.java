package net.traslated.redis;

import redis.clients.jedis.*;

import java.util.List;
import java.util.Optional;

/**
 * Utility class to manage redis connection and transaction
 */
public class RedisManager implements AutoCloseable {

    private final JedisPool pool;

    public RedisManager(JedisPool pool) {
        this.pool = pool;
    }

    public Jedis getConnection() {
        return pool.getResource();
    }

    @Override
    public void close()  {
        pool.close();
    }


    @FunctionalInterface
    public interface JedisTask<T> {
        T doInJedis(Jedis jedis);
    }

    @FunctionalInterface
    public interface JedisTaskOptional<T> {
        Optional<T> doInJedis(Jedis jedis);
    }

    @FunctionalInterface
    public interface TransactionalTask<T> {
        Response<T> doInTransaction(Transaction jedis);
    }


    /**
     * Wrap with jedis connection
     */
    public <T> T submit(JedisTask<T> jedisTask) {
        try (Jedis jedis = pool.getResource()) {
            return jedisTask.doInJedis(jedis);
        }
    }

    /**
     * Wrap with jedis connection
     * @param jedisTask
     * @param <T>
     * @return Optional<T>
     */
    public <T> Optional<T> submitOptional(JedisTaskOptional<T> jedisTask) {
        try (Jedis jedis = pool.getResource()) {
            return jedisTask.doInJedis(jedis);
        }
    }

    /**
     * Wrap in transaction
     */
    public <T> T submitTransaction(TransactionalTask<T> transaction, Jedis connection) {
        final Transaction tx = connection.multi();
        Response<T> responses = transaction.doInTransaction(tx);
        List<Object> exec = tx.exec();
        return exec == null ? null : responses.get();

    }


}
