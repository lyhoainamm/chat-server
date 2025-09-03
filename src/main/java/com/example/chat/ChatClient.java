package com.example.chat;

import jakarta.websocket.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

/**
 * Console WebSocket chat client.
 */
@ClientEndpoint
public class ChatClient {
    @OnMessage
    public void onMessage(String message) {
        System.out.println(message);
    }

    public static void main(String[] args) throws Exception {
        String uri = "ws://localhost:8025/chat";
        if (args.length > 0) {
            uri = args[0];
        }
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        Session session = container.connectToServer(ChatClient.class, URI.create(uri));
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while ((line = reader.readLine()) != null) {
            session.getBasicRemote().sendText(line);
        }
    }
}
