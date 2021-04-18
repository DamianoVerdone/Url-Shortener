package net.traslated.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.StringJoiner;

public class ErrorResponse extends Response{

    private Command command;
    private String error;

    @JsonCreator
    public ErrorResponse(@JsonProperty("command") Command command,
                    @JsonProperty("error")String error) {
        this.command = command;
        this.error = error;
    }

    @Override
    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ErrorResponse.class.getSimpleName() + "[", "]")
                .add("command=" + command)
                .add("error='" + error + "'")
                .toString();
    }
}
