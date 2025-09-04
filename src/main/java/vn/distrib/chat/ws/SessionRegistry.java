package vn.distrib.chat.ws;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class SessionRegistry {
  private final Map<String, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();

  public void add(String room, WebSocketSession session) {
    roomSessions.computeIfAbsent(room, k -> ConcurrentHashMap.newKeySet()).add(session);
  }

  public void remove(String room, WebSocketSession session) {
    var set = roomSessions.get(room);
    if (set != null) {
      set.remove(session);
      if (set.isEmpty()) {
        roomSessions.remove(room);
      }
    }
  }

  public Set<WebSocketSession> getSessions(String room) {
    return roomSessions.getOrDefault(room, Set.of());
  }
}
