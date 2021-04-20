package net.traslated.activemq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.traslated.dto.Command;
import net.traslated.dto.ErrorResponse;
import net.traslated.dto.Response;
import net.traslated.operation.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Listener waiting commands from the Clients.
 * Contains the MessageProducer to answer to the client.
 */
public class CommandListener implements MessageListener {

    private static final Logger LOG = LoggerFactory.getLogger(CommandListener.class);

    private final Protocol protocol;
    private final Session session;
    private final MessageProducer producer;
    private final Validator validator;
    private final static ObjectMapper MAPPER = new ObjectMapper();


    public CommandListener(Protocol protocol, Session session, MessageProducer producer) {
        this.protocol = protocol;
        this.session = session;
        this.producer = producer;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

    }


    /**
     * invoke when a new message is dispatched
     * @param message
     */
    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                TextMessage txtMsg = (TextMessage) message;
                Command command = MAPPER.readValue(txtMsg.getText(), Command.class);
                final Optional<Response> validationError = checkIsValidRequest(command);
                Response response = validationError.orElse(this.protocol.executeCommand(command));
                TextMessage reply = this.session.createTextMessage(MAPPER.writeValueAsString(response));
                reply.setJMSCorrelationID(message.getJMSCorrelationID());
                producer.send(reply);
            } else {
                LOG.error("Error message it is not a text message {}", message.toString());
            }

        } catch (JsonProcessingException jsonEx) {
            LOG.error("Unable to unmarshal the command from in coming message [{}] - {}", message, jsonEx);
        } catch (JMSException e) {
            //lets be optimistic no retry in this scenario
            LOG.error("ERROR - {}", e.getMessage());
        }


    }

    private Optional<Response> checkIsValidRequest(Command command) {
        final Set<ConstraintViolation<Command>> validate = validator.validate(command);
        String errorMessage = validate.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining("; "));
        return Optional.of(errorMessage).filter(s -> !s.isEmpty()).map(s -> new ErrorResponse(command, s));
    }

}
