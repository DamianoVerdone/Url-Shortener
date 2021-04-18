package net.traslated.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.StringJoiner;

public class SuccessfulResponse extends Response {

    private Command command;
    private String response;

    @JsonCreator
    public SuccessfulResponse(@JsonProperty("command") Command command,
                              @JsonProperty("response")String response) {
        this.command = command;
        this.response = response;
    }

    @Override
    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ErrorResponse.class.getSimpleName() + "[", "]")
                .add("command=" + command)
                .add("response='" + response + "'")
                .toString();
    }
}
