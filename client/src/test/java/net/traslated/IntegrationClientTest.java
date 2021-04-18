package net.traslated;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.traslated.activemq.ConnectionManager;
import net.traslated.activemq.Producer;
import net.traslated.configuration.ConfigDto;
import net.traslated.dto.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import javax.jms.JMSException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class IntegrationClientTest {
    private static ObjectMapper MAPPER = new ObjectMapper();



    public static final String ACTIVEMQ_URL = "failover://(tcp://localhost:61616)?initialReconnectDelay=3000&maxReconnectAttempts=2";

    @Test
    @Timeout(5)
    public void multipleInsertResultInOneShortUrl() throws JMSException, InterruptedException {
        final CollectResult collectResult = new CollectResult();
        final Client client = new Client(new ConfigDto(ACTIVEMQ_URL,
                "RESPONSE.TOPIC", "REQUEST.QUEUE"), collectResult);

        String url = UUID.randomUUID() + ".com";
        final Producer producer = client.getProducer();
        producer.sendCommand(new InsertCommand(url, "thisIsMyEmail@email.com"));
        producer.sendCommand(new InsertCommand(url, "thisIsMyEmail@email.com"));
        producer.sendCommand(new InsertCommand(url, "thisIsMyEmail@email.com"));
        producer.sendCommand(new InsertCommand(url, "thisIsMyEmail@email.com"));
        producer.sendCommand(new InsertCommand(url, "thisIsMyEmail@email.com"));

        final BlockingDeque<Response> responses = collectResult.responses;
        while (responses.size() < 5 ) {
            Thread.sleep(1000);
        }
        final long inserted = responses.stream().map(r -> (SuccessfulResponse) r)
                .filter(r -> !r.getResponse().equals("ALREADY PRESENT!!!"))
                .count();

        assertEquals(1, inserted);


    }

    @Test
    public void returnLongUrlAfterHaveBeenInserted() throws JMSException, InterruptedException {
        final CollectResult collectResult = new CollectResult();
        final Client client = new Client(new ConfigDto(ACTIVEMQ_URL,
                "RESPONSE.TOPIC", "REQUEST.QUEUE"), collectResult);
        final Producer producer = client.getProducer();
        String url = UUID.randomUUID() + ".com";
        producer.sendCommand(new InsertCommand(url, "thisIsMyEmail@email.com"));
        SuccessfulResponse poll = (SuccessfulResponse) collectResult.responses.poll(2, TimeUnit.SECONDS);
        producer.sendCommand(new QueryCommand(poll.getResponse()));
        poll = (SuccessfulResponse) collectResult.responses.poll(2, TimeUnit.SECONDS);
        assertEquals(url, poll.getResponse());
    }


    class CollectResult implements Consumer<String> {
        public BlockingDeque<Response> responses = new LinkedBlockingDeque<>();
        @Override
        public void accept(String s) {
            try {
                final Response response = MAPPER.readValue(s, Response.class);
                responses.add(response);
            } catch (JsonProcessingException jsonProcessingException) {
                jsonProcessingException.printStackTrace();
            }
        }
    }




}