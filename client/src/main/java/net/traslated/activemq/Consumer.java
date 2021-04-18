package net.traslated.activemq;

import javax.jms.*;

public class Consumer implements MessageListener {

    private static String MESSAGE_SELECTOR = "JMSCorrelationID = '%s'";

    public Consumer(Session session, String clientId, String topicName) throws JMSException {
        Topic topicQ = session.createTopic(topicName);
        //MessageConsumer responseListener = session.createDurableSubscriber(topicQ, clientId, String.format(MESSAGE_SELECTOR, clientId), true);
        MessageConsumer responseListener = session.createConsumer(topicQ,  String.format(MESSAGE_SELECTOR, clientId), true);
        responseListener.setMessageListener(this);

    }

    @Override
    public void onMessage(Message message) { //todo not implemented
        try {

            TextMessage txtResponse = (TextMessage) message;
            System.out.println(txtResponse.getText());
        }
        catch (Exception ex) {
        }
    }
}
