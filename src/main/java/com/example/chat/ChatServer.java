package com.example.chat;

import org.glassfish.tyrus.server.Server;

/**
 * Boots a WebSocket server that delegates message handling to {@link ChatEndpoint}.
 */
public class ChatServer {
    public static void main(String[] args) throws Exception {
        int port = 8025;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        Server server = new Server("0.0.0.0", port, "/", null, ChatEndpoint.class);
        try {
            server.start();
            System.out.println("Chat server started on ws://localhost:" + port + "/chat");
            System.out.println("Press Enter to stop the server.");
            System.in.read();
        } finally {
            server.stop();
        }
    }
}
