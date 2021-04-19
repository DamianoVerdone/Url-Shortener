package net.traslated.activemq;

import net.traslated.operation.Protocol;
import net.traslated.util.ConfigDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * It contains a pool of CommandListener (it could be generify)
 * Based on the configuration parameter it creates  (connectionNumber * threadForConnection)
 * sessions with its own MessageProducer and CommandListener. Every listener runs on his own Session thread.
 */
public class ListenersPool {

    private static final Logger LOG = LoggerFactory.getLogger(CommandListener.class);

    private final Protocol protocol;
    private final ConfigDto config;
    private final List<CommandListener> listeners;

    public ListenersPool(ActiveMQConnectionUtils activeMQConnectionUtils,
                         Protocol protocol,
                         ConfigDto config) throws JMSException {
        this.config = config;
        this.protocol = protocol;
        this.listeners = new ArrayList<>(config.getConnectionNumber() * config.getThreadForConnection());


        for (int i = config.getConnectionNumber(); i > 0; i--) {
           // shared connection among every session
            Connection connection = activeMQConnectionUtils.getConnection();
           listeners.addAll(Stream.generate(() -> initActiveMQ(connection))
                    .limit(config.getThreadForConnection())
                    .collect(Collectors.toList()));
            connection.start();

        }

    }

    /**
     *
     *
     */
    private CommandListener initActiveMQ(Connection connection) {
            try {
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Destination destination = session.createQueue(config.getQueueName());
                Topic topic = session.createTopic(config.getTopicName());
                MessageProducer producer = session.createProducer(topic);
                MessageConsumer requestListener = session.createConsumer(destination);
                final CommandListener listener = new CommandListener(protocol, session, producer);
                requestListener.setMessageListener(listener);
                return listener;
            } catch (JMSException e) {
                LOG.error("Broker Session initialization issue {}", e.getMessage());
                throw new RuntimeException("Broker Session initialization issue", e);
            }
    }




}
