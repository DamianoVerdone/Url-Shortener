package net.traslated.operation;

import net.traslated.dto.Command;
import net.traslated.dto.CommandType;
import net.traslated.dto.InsertCommand;
import net.traslated.dto.Response;

import java.util.function.Function;

public interface Operation<T extends Command> extends Function<T, Response> {

    CommandType handle();
}
