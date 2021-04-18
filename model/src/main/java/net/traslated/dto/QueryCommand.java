package net.traslated.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.StringJoiner;

public class QueryCommand extends Command {
    private final String shortUrl;

    @JsonCreator
    public QueryCommand(@JsonProperty("shortUrl") String shortUrl) {
        this.shortUrl = shortUrl;
        this.commandType = CommandType.QUERY;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", QueryCommand.class.getSimpleName() + "[", "]")
                .add("shortUrl='" + shortUrl + "'")
                .toString();
    }
}
