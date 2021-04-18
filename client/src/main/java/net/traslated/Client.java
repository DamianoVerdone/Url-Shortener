package net.traslated;

import net.traslated.configuration.ConfigDto;
import net.traslated.activemq.ConnectionManager;
import net.traslated.activemq.Consumer;
import net.traslated.activemq.Producer;
import net.traslated.dto.InsertCommand;
import net.traslated.dto.QueryCommand;
import net.traslated.dto.StatsForUrlCommand;
import net.traslated.dto.StatsForUserCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Session;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

public class Client {
    private static final Logger LOG = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) throws IOException {
        boolean exit = false;
        String clientId = UUID.randomUUID().toString();
        System.out.println("Client Id: " + clientId);
        ConfigDto config = new ConfigDto();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try (ConnectionManager conn = new ConnectionManager(config.getActivemqUrl(), clientId)) {
            Producer producer = initActiveMq(conn, clientId, config);
            System.out.println("Hello! Client coming online.");
            printHelpMessage();

            while (!exit) {
                System.out.println("Insert the action: ");
                String name = reader.readLine();
                switch (name) {
                    case "I":
                        System.out.println("Insert url:");
                        String url = reader.readLine();
                        System.out.println("Insert your email:");
                        String email = reader.readLine();
                        producer.sendCommand(new InsertCommand(url, email));
                        break;
                    case "SS":
                        System.out.println("Insert short url:");
                        String shortUrl = reader.readLine();
                        producer.sendCommand(new StatsForUrlCommand(shortUrl));
                        break;
                    case "SU":
                        System.out.println("Insert email address:");
                        String user = reader.readLine();
                        producer.sendCommand(new StatsForUserCommand(user));
                        break;
                    case "Q":
                        System.out.println("Insert short url:");
                        producer.sendCommand(new QueryCommand(reader.readLine()));
                        break;
                    case "X":
                       exit= true;
                       break;
                    case "?":
                        printHelpMessage();
                        break;
                }

            }
        } catch (JMSException e) {
            LOG.error("Error initializing ActiveMQ communication {}", e.getMessage());
        }


    }



    private static Producer initActiveMq(ConnectionManager conn, String clientId, ConfigDto config) throws JMSException {
        Session session = conn.createSession();
        Consumer consumer = new Consumer(session, clientId, config.getTopicName());
        return new Producer(session, clientId,  config.getQueueName());
    }


    private static void printHelpMessage() {
        System.out.println(
                "Command the Client with:\n" +
                        "  I - Insert\n" +
                        "  Q - Query\n" +
                        "  SS - Statistics by short url\n" +
                        "  SU - Statistics by user\n" +
                        "  ? - this message\n" +
                        "  X - Exit");
    }
}