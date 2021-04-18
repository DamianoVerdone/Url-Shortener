package net.traslated.operation;

import net.traslated.dto.*;
import net.traslated.redis.RedisManager;
import net.traslated.redis.ShortUrlDto;
import net.traslated.util.Constants;

import java.util.Optional;


public class StatsForUrlOperation implements Operation<StatsForUrlCommand>{

    private final RedisManager redisManager;

    public StatsForUrlOperation(RedisManager redisManager) {
        this.redisManager = redisManager;
    }

    @Override
    public Response apply(StatsForUrlCommand statsForUrlCommand) {
       return getStatisticByShortUrl(statsForUrlCommand.getShortUrl())
               .<Response>map(count -> new SuccessfulResponse(statsForUrlCommand, count.toString()))
               .orElse(new ErrorResponse(statsForUrlCommand, "NOT FOUND!!!"));
    }

    private Optional<Long> getStatisticByShortUrl(String shortUrl) {
        String key = String.format(Constants.SHORT_URL_INFO, shortUrl);
        return redisManager.submitOptional(jedis -> Optional.ofNullable(jedis.hget(key, ShortUrlDto.ACCESS_COUNTER)))
                .map(Long::valueOf);
    }

    @Override
    public CommandType handle() {
        return CommandType.STATS_FOR_SHORT_URL;
    }
}
