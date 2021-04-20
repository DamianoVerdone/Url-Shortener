package net.traslated;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.traslated.activemq.Producer;
import net.traslated.configuration.ConfigDto;
import net.traslated.dto.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import javax.jms.JMSException;
import java.util.UUID;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IntegrationClientTest {
    private static ObjectMapper MAPPER = new ObjectMapper();



    public static final String ACTIVEMQ_URL = "failover://(tcp://localhost:61616)?initialReconnectDelay=3000&maxReconnectAttempts=2";

    @Test
    @Timeout(8)
    public void multipleInsertResultInOneShortUrl() throws JMSException, InterruptedException {
        final CollectResult collectResult = new CollectResult();
        final Client client = new Client(new ConfigDto(ACTIVEMQ_URL,
                "RESPONSE.TOPIC", "REQUEST.QUEUE"), collectResult);

        String url = "http://" + UUID.randomUUID() + ".com";
        String user = UUID.randomUUID().toString() + "@emai.com";
        final Producer producer = client.getProducer();
        producer.sendCommand(new InsertCommand(url, user));
        producer.sendCommand(new InsertCommand(url, user));
        producer.sendCommand(new InsertCommand(url, user));
        producer.sendCommand(new InsertCommand(url, user));
        producer.sendCommand(new InsertCommand(url, user));
        producer.sendCommand(new InsertCommand(url, user));
        producer.sendCommand(new InsertCommand(url, user));

        final BlockingDeque<Response> responses = collectResult.responses;

        while (responses.size() < 7 ) {
            Thread.sleep(1000);
        }
        final long inserted = responses.stream().map(r -> (SuccessfulResponse) r)
                .filter(r -> !r.getResponse().equals("ALREADY PRESENT!!!"))
                .count();

        assertEquals(1, inserted);

        //check user contribution
        responses.clear();
        producer.sendCommand(new StatsForUserCommand(user));
        final SuccessfulResponse poll = (SuccessfulResponse) responses.poll(2, TimeUnit.SECONDS);
        assertEquals("1", poll.getResponse());


    }

    @Test
    public void returnLongUrlAfterHaveBeenInserted() throws JMSException, InterruptedException {
        final CollectResult collectResult = new CollectResult();
        final Client client = new Client(new ConfigDto(ACTIVEMQ_URL,
                "RESPONSE.TOPIC", "REQUEST.QUEUE"), collectResult);
        final Producer producer = client.getProducer();
        String url = "http://" + UUID.randomUUID() + ".com";
        producer.sendCommand(new InsertCommand(url, "thisIsMyEmail@email.com"));
        SuccessfulResponse insertResponse = (SuccessfulResponse) collectResult.responses.poll(2, TimeUnit.SECONDS);
        final String shortUrl = insertResponse.getResponse();
        producer.sendCommand(new QueryCommand(shortUrl));
        SuccessfulResponse queryResponse = (SuccessfulResponse) collectResult.responses.poll(2, TimeUnit.SECONDS);
        assertEquals(url, queryResponse.getResponse());

        //check access times increase with two more
        producer.sendCommand(new QueryCommand(shortUrl));
        producer.sendCommand(new QueryCommand(shortUrl));
        collectResult.responses.poll(2, TimeUnit.SECONDS);
        collectResult.responses.poll(2, TimeUnit.SECONDS);

        producer.sendCommand(new StatsForUrlCommand(shortUrl));
        final SuccessfulResponse statistic = (SuccessfulResponse)collectResult.responses.poll(2, TimeUnit.SECONDS);
        assertEquals("3", statistic.getResponse());

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