package net.traslated.activemq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

public class Consumer implements MessageListener {

    private static String MESSAGE_SELECTOR = "JMSCorrelationID = '%s'";
    private static final Logger LOG = LoggerFactory.getLogger(Consumer.class);

    private final java.util.function.Consumer<String> processTheMessage;


    public Consumer(Session session, String clientId,
                    String topicName,
                    java.util.function.Consumer<String> processTheMessage ) throws JMSException {
        this.processTheMessage = processTheMessage;
        Topic topicQ = session.createTopic(topicName);
        //MessageConsumer responseListener = session.createDurableSubscriber(topicQ, clientId, String.format(MESSAGE_SELECTOR, clientId), true);
        MessageConsumer responseListener = session.createConsumer(topicQ,  String.format(MESSAGE_SELECTOR, clientId), true);
        responseListener.setMessageListener(this);

    }

    @Override
    public void onMessage(Message message) { //todo not implemented
        try {

            TextMessage txtResponse = (TextMessage) message;
            processTheMessage.accept(txtResponse.getText());
        } catch (JMSException e) {
            //lets be optimistic no retry in this scenario
            LOG.error("ERROR - {}", e.getMessage());
        }

    }
}
