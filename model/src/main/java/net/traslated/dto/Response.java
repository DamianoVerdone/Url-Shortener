package net.traslated.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeInfo(use = NAME, include = PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type( value = InsertCommand.class, name = "SUCCESS" ),
        @JsonSubTypes.Type( value = QueryCommand.class, name = "ERROR" )
})
public  abstract class Response {
    private Command command;

    public Command getCommand() {
        return command;
    }


}
