package vn.distrib.chat.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import vn.distrib.chat.model.ChatMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {
  private final SessionRegistry registry;
  private final RabbitTemplate rabbit;
  private final ObjectMapper om = new ObjectMapper();

  @Value("${app.amqp.exchange}")
  private String exchangeName;

  public ChatWebSocketHandler(SessionRegistry registry, RabbitTemplate rabbit) {
    this.registry = registry;
    this.rabbit = rabbit;
  }

  private static MultiValueMap<String, String> query(URI uri) {
    return UriComponentsBuilder.fromUri(uri).build().getQueryParams();
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    var q = query(session.getUri());
    var room = q.getFirst("room");
    var user = q.getFirst("user");
    if (room == null || user == null) {
      session.close(CloseStatus.BAD_DATA);
      return;
    }
    session.getAttributes().put("room", room);
    session.getAttributes().put("user", user);
    registry.add(room, session);

    var join = new ChatMessage(room, "system", user + " joined", System.currentTimeMillis());
    rabbit.convertAndSend(exchangeName, "room." + room, om.writeValueAsString(join));
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    var room = (String) session.getAttributes().get("room");
    var user = (String) session.getAttributes().get("user");
    var msg = new ChatMessage(room, user, message.getPayload(), System.currentTimeMillis());
    rabbit.convertAndSend(exchangeName, "room." + room, om.writeValueAsString(msg));
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    var room = (String) session.getAttributes().get("room");
    var user = (String) session.getAttributes().get("user");
    registry.remove(room, session);
    if (room != null && user != null) {
      var leave = new ChatMessage(room, "system", user + " left", System.currentTimeMillis());
      rabbit.convertAndSend(exchangeName, "room." + room, om.writeValueAsString(leave));
    }
  }

  public void broadcast(ChatMessage msg) {
    var text = toText(msg);
    for (var sess : registry.getSessions(msg.room())) {
      try {
        sess.sendMessage(text);
      } catch (Exception ignored) {
      }
    }
  }

  private TextMessage toText(ChatMessage m) {
    try {
      return new TextMessage(om.writeValueAsString(m).getBytes(StandardCharsets.UTF_8));
    } catch (Exception e) {
      return new TextMessage("{\"error\":\"serialize\"}");
    }
  }
}
