package net.traslated;

import net.traslated.activemq.ConnectionManager;
import net.traslated.activemq.Consumer;
import net.traslated.activemq.Producer;
import net.traslated.configuration.ConfigDto;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import java.io.Closeable;
import java.io.IOException;
import java.util.UUID;

/**
 * A Class that group the Connection the consumer and the producer in one place
 * Makes create test easier
 */
public class Client implements Closeable {

    private final ConnectionManager connectionManager;
    private final String clientId ;
    private final Producer producer;

    public Client(ConfigDto config, java.util.function.Consumer<String> processor) throws JMSException {
        this.clientId = UUID.randomUUID().toString();
        this.connectionManager = new ConnectionManager(config.getActivemqUrl(), clientId);
        this.producer =  initActiveMq(connectionManager, clientId, config, processor);
    }


    private  Producer initActiveMq(ConnectionManager conn,
                                   String clientId,
                                   ConfigDto config,
                                   java.util.function.Consumer<String> processor) throws JMSException {
        Session session = conn.createSession();
        Consumer consumer = new Consumer(session, clientId, config.getTopicName(), processor);
        return new Producer(session, clientId,  config.getQueueName());
    }

    @Override
    public void close()  {
        connectionManager.close();
    }

    public String getClientId() {
        return clientId;
    }

    public Producer getProducer() {
        return producer;
    }
}
