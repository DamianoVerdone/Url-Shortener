package net.traslated.operation;

import net.traslated.dto.*;
import net.traslated.redis.RedisManager;
import net.traslated.redis.ShortUrlDto;

import java.util.Optional;

import static net.traslated.util.Constants.SHORT_URL_INFO;

public class QueryOperation implements Operation<QueryCommand> {

    private final RedisManager redisManager;

    public QueryOperation(RedisManager redisManager) {
        this.redisManager = redisManager;
    }

    @Override
    public Response apply(QueryCommand queryCommand) {
        return getLongUrl(queryCommand.getShortUrl())
                .<Response>map(s -> {
                    incrNumberAccesses(queryCommand.getShortUrl());
                    return new SuccessfulResponse(queryCommand, s);
                })
                .orElse(new ErrorResponse(queryCommand, "NOT FOUND!!!"));

    }


    private Optional<String> getLongUrl(String shortUrl) {
        String key = String.format(SHORT_URL_INFO, shortUrl);
        return redisManager.submitOptional(
                jedis -> Optional.ofNullable(jedis.hget(key, ShortUrlDto.LONG_URL))
        );

    }

    private void incrNumberAccesses(String shortUrl) {
        String key = String.format(SHORT_URL_INFO, shortUrl);
        redisManager.submit(jedis -> jedis.hincrBy(key, ShortUrlDto.ACCESS_COUNTER, 1));

    }

    @Override
    public CommandType handle() {
        return CommandType.QUERY;
    }
}
