package net.traslated.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;

public class ConnectionManager {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectionManager.class);
    private final Connection connection;

    public ConnectionManager(String connectionUri, String clientName) throws JMSException {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(connectionUri);
        Connection connection = connectionFactory.createConnection();
        connection.setClientID(clientName);
        connection.start();
        this.connection = connection;
    }

    public Session createSession() throws JMSException {
        return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    /**
     * There is no need to close the sessions, producers, and consumers of a
     * closed connection.
     */
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (JMSException e) {
                LOG.error("Error closing the ActiveMQ connection: {}", e.getMessage());
            }
        }
    }


}
