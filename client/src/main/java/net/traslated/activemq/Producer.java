package net.traslated.activemq;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.traslated.dto.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

public class Producer {

    private final Session session;
    private final MessageProducer producer;
    private final String clientId;
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger LOG = LoggerFactory.getLogger(Producer.class);

    public Producer(Session session, String clientId, String queueName) throws JMSException {
        Queue destination = session.createQueue(queueName);
        this.session =session;
        this.producer = session.createProducer(destination);
        this.clientId = clientId;
    }

    public void sendCommand(Command command) {
        try {
            TextMessage stringMessage = session.createTextMessage(objectMapper.writeValueAsString(command));
            stringMessage.setJMSCorrelationID(clientId);
            producer.send(stringMessage);
        } catch (Exception e) {
            LOG.error("Error sending command: {} exception {}", command, e.getMessage());
        }

    }
}
