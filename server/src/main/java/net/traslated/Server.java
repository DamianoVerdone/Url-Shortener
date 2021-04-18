package net.traslated;

import net.traslated.activemq.ActiveMQConnectionUtils;
import net.traslated.activemq.ListenersPool;
import net.traslated.operation.*;
import net.traslated.redis.RedisManager;
import net.traslated.redis.RedisManagerFactory;
import net.traslated.util.ConfigDto;
import redis.clients.jedis.JedisPoolConfig;

import javax.jms.JMSException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Server {
    public static void main(String[] args)  {
        String env = "";
        if (args.length > 0)
            env = "-" + args[0];
        ConfigDto config = new ConfigDto("config" + env);

        try (RedisManager redisManager = initRedisManager(config);
             ActiveMQConnectionUtils activeMQConnectionUtils = new ActiveMQConnectionUtils(config)) {
            Protocol protocol = new Protocol(List.of(
                    new InsertOperation(redisManager),
                    new QueryOperation(redisManager),
                    new StatsForUrlOperation(redisManager),
                    new StatsForUserOperation(redisManager)));
            final ListenersPool listenersPool = new ListenersPool(activeMQConnectionUtils, protocol, config);
            System.out.println("Server started.");
            System.out.println("Use `echo -n X | nc localhost " + config.getShutdownPort() + "` to stop");
            waitForShutDownCommand(config.getShutdownPort());
            listenersPool.shutdown();
        } catch (JMSException | IOException | InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }


    private static RedisManager initRedisManager(ConfigDto config) {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig(); //fixme add proper configuration
        return RedisManagerFactory.createManager(config.getRedisHost(), config.getRedisPort(), jedisPoolConfig);
    }

    public static void waitForShutDownCommand(int port) throws IOException {
        try (ServerSocket server = new ServerSocket(port)) {
            while (true) {
                Socket socket = server.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String message = in.readLine();
                in.close();
                socket.close();
                if (message.equalsIgnoreCase("X")) break;
            }
            System.out.println("Shutting down server!!");
        }
    }

}
