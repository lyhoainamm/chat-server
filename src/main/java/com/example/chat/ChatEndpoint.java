package com.example.chat;

import com.rabbitmq.client.*;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket endpoint that publishes incoming messages to RabbitMQ and
 * broadcasts messages consumed from RabbitMQ to connected WebSocket clients.
 */
@ServerEndpoint("/chat")
public class ChatEndpoint {
    private static final String EXCHANGE = "chat";
    private static final ConnectionFactory factory = new ConnectionFactory();
    private static Connection connection;
    private static Channel channel;
    private static String queueName;

    private static final Set<Session> sessions = ConcurrentHashMap.newKeySet();

    static {
        factory.setHost("localhost");
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE, BuiltinExchangeType.FANOUT);
            queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, EXCHANGE, "");
            channel.basicConsume(queueName, true, (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                for (Session session : sessions) {
                    try {
                        session.getBasicRemote().sendText(message);
                    } catch (IOException e) {
                        // ignore send failures
                    }
                }
            }, consumerTag -> { });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
    }

    @OnMessage
    public void onMessage(String message) throws IOException {
        channel.basicPublish(EXCHANGE, "", null, message.getBytes(StandardCharsets.UTF_8));
    }
}
