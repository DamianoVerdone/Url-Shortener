package net.traslated.operation;

import net.traslated.dto.*;
import net.traslated.redis.RedisManager;
import net.traslated.redis.ShortUrlDto;
import net.traslated.util.Constants;
import net.traslated.util.GenerateShortUrlId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Optional;

import static net.traslated.util.Constants.*;

public class InsertOperation implements Operation<InsertCommand> {

    private static final Logger LOG = LoggerFactory.getLogger(InsertOperation.class);

    private final RedisManager redisManager;

    public InsertOperation(RedisManager redisManager) {
        this.redisManager = redisManager;
    }

    @Override
    public Response apply(InsertCommand insertCommand) {
        ShortUrlDto shortUrlDto = new ShortUrlDto(insertCommand);
        try (Jedis conn = redisManager.getConnection()) {
            if (checkIfLongUrlExist(insertCommand.getUrl(), conn)) {
                return new SuccessfulResponse(insertCommand, "ALREADY PRESENT!!!");
            }
            Optional<String> shortUrlsId = GenerateShortUrlId.INSTANCE.getShortUrlsId(getCounterCounterFromRedis());
            if (shortUrlsId.isEmpty()) {
                return new ErrorResponse(insertCommand, "Sorry we finished the space!!");
            }

            if (saveShortUrl(shortUrlDto, shortUrlsId.get(), conn)) {
                return new SuccessfulResponse(insertCommand, shortUrlsId.get());
            } else {
                throw new ConcurrentModificationException("Concurrent modification error inserting : " + insertCommand);
            }
        }
    }

    private Boolean checkIfLongUrlExist(String longUrl, Jedis conn) {
        String key = String.format(LONG_URL_TEMP_WATCH, longUrl);
        final Pipeline pipelined = conn.pipelined();
        pipelined.watch(key);
        redis.clients.jedis.Response<List<Boolean>> res = pipelined.smismember(LONG_URL_INSERTED, longUrl);
        pipelined.sync();
       return res.get().stream().reduce(true, (a, b)-> a && b);

    }



    private Boolean saveShortUrl(ShortUrlDto shortUrlDto, String shortUrl, Jedis connectionWithWatch) {
        String key = String.format(SHORT_URL_INFO, shortUrl);
        final String optimisticLockKey = String.format(LONG_URL_TEMP_WATCH, shortUrlDto.getLongUrl());

        Long nAddedUrl = redisManager.submitTransaction(
                jedis -> {
                    jedis.hset(key, shortUrlDto.toMap());
                    jedis.hincrBy(USER_CONTRIBUTION, shortUrlDto.getUser(), 1);
                    jedis.incr(optimisticLockKey);
                    jedis.del(optimisticLockKey);
                    return jedis.sadd(LONG_URL_INSERTED, shortUrlDto.getLongUrl());
                }, connectionWithWatch);
        return nAddedUrl!= null && nAddedUrl >0;
    }

    /**
     *
     * @return
     */
    private Long getCounterCounterFromRedis() {
        return redisManager.submit(jedis -> jedis.incr(Constants.SHORT_URL_COUNTER));
    }

    @Override
    public CommandType handle() {
        return CommandType.INSERT;
    }
}
