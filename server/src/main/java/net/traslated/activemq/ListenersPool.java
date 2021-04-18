package net.traslated.activemq;

import net.traslated.operation.Protocol;
import net.traslated.util.ConfigDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * It contains a pool of CommandListener (it could be generify)
 * Based on the configuration parameter it creates  (connectionNumber * threadForConnection)
 * sessions with its own MessageProducer and CommandListener. Every listener runs on his own thread.
 */
public class ListenersPool {

    private final ExecutorService executorService;
    private final Protocol protocol;
    private final ConfigDto config;

    public ListenersPool(ActiveMQConnectionUtils activeMQConnectionUtils,
                         Protocol protocol,
                         ConfigDto config) throws JMSException, InterruptedException {
        this.config = config;
        this.executorService = Executors.newFixedThreadPool(config.getConnectionNumber() * config.getThreadForConnection());
        this.protocol = protocol;

        for (int i = config.getConnectionNumber(); i > 0; i--) {
            //shared connection among every session
            Connection connection = activeMQConnectionUtils.getConnection();
            executorService.invokeAll(Stream.generate(() -> initActiveMQ(connection))
                    .limit(config.getThreadForConnection())
                    .collect(Collectors.toList()));
            connection.start();

        }

    }

    /**
     * Creates a new consumer and producer to be used in the new command listener
     */
    private Callable<CommandListener> initActiveMQ(Connection connection) {
        return () -> {
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(config.getQueueName());
            Topic topic = session.createTopic(config.getTopicName());
            MessageProducer producer = session.createProducer(topic);
            MessageConsumer requestListener = session.createConsumer(destination);
            final CommandListener listener = new CommandListener(protocol, session, producer);
            requestListener.setMessageListener(listener);
            return listener;

        };
    }

    /**
     * Stops all the listeners
     */
    public void shutdown() {
        executorService.shutdownNow();
    }
}
