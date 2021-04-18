package net.traslated.operation;

import net.traslated.dto.Command;
import net.traslated.dto.CommandType;
import net.traslated.dto.Response;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Based on the command type find the proper Operation and execute it
 */
public class Protocol {

    private  Map<CommandType,  Operation<Command>> dispatcher;

    @SuppressWarnings("unchecked")
    public Protocol(List<Operation> operations) {
        dispatcher = operations.stream().collect(Collectors.toUnmodifiableMap(Operation::handle, op -> op));
    }

    public Response executeCommand(Command command) {
      return   dispatcher.get(command.getCommandType()).apply(command);
    }
}
