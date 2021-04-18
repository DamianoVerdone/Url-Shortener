package net.traslated.activemq;

import net.traslated.util.ConfigDto;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQPrefetchPolicy;
import org.apache.activemq.pool.PooledConnectionFactory;

import javax.jms.Connection;
import javax.jms.JMSException;

/**
 * Utility class to create a connection pool from the configuration.
 */
public class ActiveMQConnectionUtils implements AutoCloseable {


    private final PooledConnectionFactory pooledConnectionFactory;


    public ActiveMQConnectionUtils(ConfigDto configDto) {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(configDto.getActivemqUrl());
        final ActiveMQPrefetchPolicy prefetchPolicy = new ActiveMQPrefetchPolicy();
        prefetchPolicy.setQueuePrefetch(1);
        activeMQConnectionFactory.setPrefetchPolicy(prefetchPolicy);
        pooledConnectionFactory = new PooledConnectionFactory(activeMQConnectionFactory);
        // pooledConnectionFactory.setExpiryTimeout(2000);
        pooledConnectionFactory.setMaximumActiveSessionPerConnection(configDto.getThreadForConnection());
        pooledConnectionFactory.setMaxConnections(configDto.getConnectionNumber());
        pooledConnectionFactory.setReconnectOnException(true);


    }

    /**
     * Creates a new connection, or picks up one from the pool.
     *
     * @return connection
     * @throws JMSException
     */
    public Connection getConnection() throws JMSException {
        return pooledConnectionFactory.createConnection();
    }

    @Override
    public void close() {
        if (pooledConnectionFactory != null) {
            pooledConnectionFactory.clear();
        }
    }
}
