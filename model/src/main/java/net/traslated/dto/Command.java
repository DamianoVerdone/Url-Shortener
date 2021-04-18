package net.traslated.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeInfo(use = NAME, include = EXISTING_PROPERTY, property = "commandType", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type( value = InsertCommand.class, name = "INSERT" ),
        @JsonSubTypes.Type( value = QueryCommand.class, name = "QUERY" ),
        @JsonSubTypes.Type( value = StatsForUserCommand.class, name = "STATS_FOR_USER" ),
        @JsonSubTypes.Type( value = StatsForUrlCommand.class, name = "STATS_FOR_SHORT_URL" )
})
public abstract class Command  {

    protected CommandType commandType;

    public CommandType getCommandType() {
        return commandType;
    }

}



