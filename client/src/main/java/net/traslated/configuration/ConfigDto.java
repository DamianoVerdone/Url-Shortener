package net.traslated.configuration;

import java.util.ResourceBundle;

public class ConfigDto {
    private static final String ACTITVEMQ_URL = "actitvemq.url";
    private static final String RESPONSE_TOPIC = "response.topic";
    private static final String REQUEST_QUEUE = "request.queue";
    private final String activemqUrl;
    private final String topicName;
    private final String queueName;

    public ConfigDto(String activemqUrl, String topicName, String queueName) {
        this.activemqUrl = activemqUrl;
        this.topicName = topicName;
        this.queueName = queueName;
    }

    public ConfigDto() {
        ResourceBundle config = ResourceBundle.getBundle("config");
        activemqUrl = config.getString(ACTITVEMQ_URL);
        topicName = config.getString(RESPONSE_TOPIC);
        queueName = config.getString(REQUEST_QUEUE);
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
}
