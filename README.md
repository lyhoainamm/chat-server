
# Distributed Chat System

Sample distributed chat built with Spring Boot WebSocket, RabbitMQ and a Java Swing client.

## Server

Build the server:

# chat-server

Distributed chat system using WebSocket and RabbitMQ.

## Build

Compile the project with Maven (requires JDK 21+):


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

## Run

Start RabbitMQ locally and then launch the WebSocket server (default port 8025):

```sh
java -cp target/chat-server-1.0-SNAPSHOT.jar com.example.chat.ChatServer [port]
```

Start a console WebSocket client:

```sh
java -cp target/chat-server-1.0-SNAPSHOT.jar com.example.chat.ChatClient [ws://host:port/chat]
```

Tested with OpenJDK 21 and compatible with JDK 22.

