package net.traslated.util;

import java.util.ResourceBundle;

public class ConfigDto {
    private static final String ACTITVEMQ_URL = "actitvemq.url";
    private static final String RESPONSE_TOPIC = "response.topic";
    private static final String REQUEST_QUEUE = "request.queue";
    private static final String REDIS_HOST = "redis.host";
    private static final String REDIS_PORT = "redis.port";
    private static final String CONNECTION_NUMBER = "actitvemq.connection.number";
    private static final String THREAD_NUMBER = "actitvemq.connection.thread"; //1 thread 1 session
    private static final String ACTITVEMQ_PREFETCH = "actitvemq.prefetch";
    private static final String SHUTDOWN_PORT = "shutdown.port";

    private final String activemqUrl;
    private final String topicName;
    private final String queueName;
    private final String redisHost;
    private final Integer redisPort;
    private final Integer connectionNumber;
    private final Integer threadForConnection;
    private final Integer activemqPrefetch;
    private final Integer shutdownPort;


    public ConfigDto(String name) {
        ResourceBundle config = ResourceBundle.getBundle(name);
        activemqUrl = config.getString(ACTITVEMQ_URL);
        topicName = config.getString(RESPONSE_TOPIC);
        queueName = config.getString(REQUEST_QUEUE);
        redisPort = Integer.valueOf(config.getString(REDIS_PORT));
        redisHost = config.getString(REDIS_HOST);
        connectionNumber = Integer.valueOf(config.getString(CONNECTION_NUMBER));
        threadForConnection = Integer.valueOf(config.getString(THREAD_NUMBER));
        activemqPrefetch = Integer.valueOf(config.getString(ACTITVEMQ_PREFETCH));
        shutdownPort = Integer.valueOf(config.getString(SHUTDOWN_PORT));

    }

    public String getActivemqUrl() {
        return activemqUrl;
    }

    public String getTopicName() {
        return topicName;
    }

    public String getQueueName() {
        return queueName;
    }

    public String getRedisHost() {
        return redisHost;
    }

    public Integer getRedisPort() {
        return redisPort;
    }

    public Integer getConnectionNumber() {
        return connectionNumber;
    }

    public Integer getThreadForConnection() {
        return threadForConnection;
    }

    public Integer getActivemqPrefetch() {
        return activemqPrefetch;
    }

    public Integer getShutdownPort() {
        return shutdownPort;
    }
}
