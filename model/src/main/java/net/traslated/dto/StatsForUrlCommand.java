package net.traslated.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.StringJoiner;

public class StatsForUrlCommand extends Command{
    private final String shortUrl;

    @JsonCreator
    public StatsForUrlCommand(@JsonProperty("shortUrl") String shortUrl) {
        this.shortUrl = shortUrl;
        this.commandType = CommandType.STATS_FOR_SHORT_URL;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StatsForUrlCommand.class.getSimpleName() + "[", "]")
                .add("shortUrl='" + shortUrl + "'")
                .toString();
    }
}
