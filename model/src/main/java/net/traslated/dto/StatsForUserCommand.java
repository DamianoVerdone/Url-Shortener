package net.traslated.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.StringJoiner;

public class StatsForUserCommand extends Command{

    private final String user;
    @JsonCreator
    public StatsForUserCommand(@JsonProperty("user") String user) {
        this.user = user;
        this.commandType = CommandType.STATS_FOR_USER;
    }

    public String getUser() {
        return user;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StatsForUserCommand.class.getSimpleName() + "[", "]")
                .add("user='" + user + "'")
                .toString();
    }
}
