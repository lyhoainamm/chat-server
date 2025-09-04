package vn.distrib.chat.client;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class ChatClientFrame extends JFrame implements WebSocket.Listener {
  private final JTextArea taLog = new JTextArea();
  private final JTextField tfServer = new JTextField("ws://localhost:8080/ws/chat");
  private final JTextField tfRoom = new JTextField("general");
  private final JTextField tfUser = new JTextField("user" + (System.currentTimeMillis()%1000));
  private final JTextField tfMsg = new JTextField();
  private final JButton btnConnect = new JButton("Connect");
  private final JButton btnSend = new JButton("Send");

  private volatile WebSocket ws;
  private final ObjectMapper om = new ObjectMapper();

  public ChatClientFrame() {
    super("Distributed Chat - Swing");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(720, 480);
    setLocationRelativeTo(null);

    taLog.setEditable(false);

    var north = new JPanel(new GridLayout(2,1));
    var row1 = new JPanel(new GridLayout(1,3));
    row1.add(labeled("Server", tfServer));
    row1.add(labeled("Room", tfRoom));
    row1.add(labeled("User", tfUser));
    var row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    row2.add(btnConnect);
    north.add(row1);
    north.add(row2);

    var south = new JPanel(new BorderLayout());
    south.add(tfMsg, BorderLayout.CENTER);
    south.add(btnSend, BorderLayout.EAST);

    add(north, BorderLayout.NORTH);
    add(new JScrollPane(taLog), BorderLayout.CENTER);
    add(south, BorderLayout.SOUTH);

    btnConnect.addActionListener(e -> connect());
    btnSend.addActionListener(e -> sendMessage());
    tfMsg.addActionListener(e -> sendMessage());
  }

  private JPanel labeled(String s, JComponent c){
    var p = new JPanel(new BorderLayout(6,0));
    p.add(new JLabel(s), BorderLayout.WEST);
    p.add(c, BorderLayout.CENTER);
    return p;
  }

  private void connect() {
    try {
      var uri = URI.create(tfServer.getText().trim()
          + "?room=" + tfRoom.getText().trim()
          + "&user=" + tfUser.getText().trim());
      ws = HttpClient.newHttpClient().newWebSocketBuilder().buildAsync(uri, this).join();
      log("Connected to " + uri);
    } catch (Exception ex) {
      log("Connect error: " + ex.getMessage());
    }
  }

  private void sendMessage() {
    if (ws == null) {
      log("Not connected.");
      return;
    }
    var text = tfMsg.getText().trim();
    if (text.isEmpty()) return;
    ws.sendText(text, true);
    tfMsg.setText("");
  }

  private void log(String s){
    SwingUtilities.invokeLater(() -> taLog.append(s + "\n"));
  }

  @Override
  public void onOpen(WebSocket webSocket) {
    webSocket.request(1);
  }

  @Override
  public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
    try {
      var node = om.readTree(data.toString());
      var room = node.get("room").asText();
      var from = node.get("from").asText();
      var text = node.get("text").asText();
      var ts = node.get("ts").asLong();
      log("[" + room + "] " + Instant.ofEpochMilli(ts) + " <" + from + ">: " + text);
    } catch (Exception ignore) {
      log(data.toString());
    }
    webSocket.request(1);
    return CompletableFuture.completedFuture(null);
  }

  @Override
  public void onError(WebSocket webSocket, Throwable error) {
    log("WS error: " + error.getMessage());
  }

  @Override
  public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
    log("Closed: " + statusCode + " " + reason);
    return CompletableFuture.completedFuture(null);
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new ChatClientFrame().setVisible(true));
  }
}
