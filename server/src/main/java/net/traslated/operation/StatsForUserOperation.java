package net.traslated.operation;

import net.traslated.dto.*;
import net.traslated.redis.RedisManager;

import java.util.Optional;

import static net.traslated.util.Constants.USER_CONTRIBUTION;

/**
 * Takes a userId and return the number of urls successful inserted by the user
 */
public class StatsForUserOperation implements Operation<StatsForUserCommand>{

    private final RedisManager redisManager;

    public StatsForUserOperation(RedisManager redisManager) {
        this.redisManager = redisManager;
    }

    @Override
    public Response apply(StatsForUserCommand statsForUserCommand) {
        return getStatisticByUser(statsForUserCommand.getUser())
                .<Response>map(counter -> new SuccessfulResponse(statsForUserCommand, counter.toString()))
                .orElse(new ErrorResponse(statsForUserCommand, "NOT FOUND!!!"));
    }

    @Override
    public CommandType handle() {
        return CommandType.STATS_FOR_USER;
    }

    private Optional<Long> getStatisticByUser(String email) {
        return redisManager.submitOptional(jedis -> Optional.ofNullable(jedis.hget(USER_CONTRIBUTION, email)))
                .map(Long::valueOf);
    }
}
