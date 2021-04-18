package net.traslated.redis;

import net.traslated.dto.InsertCommand;

import java.util.Map;

public class ShortUrlDto {

    public static final String USER = "USER";
    public static final String ACCESS_COUNTER = "ACCESS_COUNTER";
    public static final String LONG_URL = "LONG_URL";
    private final String user;
    private final Integer accessNumber;
    private final String longUrl;

    public ShortUrlDto(InsertCommand insertCommand) {
        this.user = insertCommand.getEmail();
        this.longUrl = insertCommand.getUrl();
        this.accessNumber = 0;
    }


    public String getUser() {
        return user;
    }


    public int getAccessNumber() {
        return accessNumber;
    }


    public String getLongUrl() {
        return longUrl;
    }


    public Map<String, String> toMap() {
        return Map.of(USER, this.user, ACCESS_COUNTER, accessNumber.toString(), LONG_URL, longUrl);

    }
}
