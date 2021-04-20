package net.traslated.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Email;
import java.util.StringJoiner;

public class InsertCommand extends Command {
    @URL
    private final String url;
    @Email
    private final String email;

    @JsonCreator()
    public InsertCommand(@JsonProperty("longUrl") String longUrl, @JsonProperty("email")String email) {
        this.commandType = CommandType.INSERT;
        this.url = longUrl;
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", InsertCommand.class.getSimpleName() + "[", "]")
                .add("url='" + url + "'")
                .add("email='" + email + "'")
                .toString();
    }
}

