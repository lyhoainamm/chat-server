package vn.distrib.chat.amqp;

import com.fasterxml.jackson.databind.ObjectMapper;
import vn.distrib.chat.model.ChatMessage;
import vn.distrib.chat.ws.ChatWebSocketHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ChatConsumer {
  private final ChatWebSocketHandler wsHandler;
  private final ObjectMapper om = new ObjectMapper();

  public ChatConsumer(ChatWebSocketHandler wsHandler) {
    this.wsHandler = wsHandler;
  }

  @RabbitListener(queues = "#{nodeQueue.name}")
  public void onMessage(String json) throws Exception {
    var msg = om.readValue(json, ChatMessage.class);
    wsHandler.broadcast(msg);
  }
}
