# chat-server

Distributed chat system using WebSocket and RabbitMQ.

## Build

Compile the project with Maven (requires JDK 21+):

```sh
mvn package
```

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
