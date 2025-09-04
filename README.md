# Distributed Chat System

Sample distributed chat built with Spring Boot WebSocket, RabbitMQ and a Java Swing client.

## Server

Build the server:

```sh
mvn package
```

Run a node (RabbitMQ must be running on localhost):

```sh
java -jar target/chat-server-1.0.0.jar
```

Run an additional node on another port:

```sh
java -jar target/chat-server-1.0.0.jar --server.port=8081
```

## Client

Build the Swing client:

```sh
cd chat-client-swing
mvn package
```

Start the UI:

```sh
java -cp target/chat-client-swing-1.0.0.jar vn.distrib.chat.client.ChatClientFrame
```

Connect multiple clients to different server nodes using URLs like `ws://localhost:8080/ws/chat?room=general&user=alice`.
