package com.goldenratio.chuglikaro.Services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class SocketHandler extends TextWebSocketHandler {

    List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    private void handlePeerDisconnection(WebSocketSession session) throws IOException {
        // Remove the disconnected peer from the list
        sessions.remove(session);

        // Close the WebSocket session
        session.close();
    }
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        System.out.println(session);  
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println(status);
    }

    @Override
    public void handleTextMessage(WebSocketSession senderSession, TextMessage message) throws IOException {
//    	List<WebSocketSession> alreadySent = new CopyOnWriteArrayList<>();
//    	
//    	for (WebSocketSession session : sessions) {
//    		if(sessions.size()%2==0 && !session.getId().equals(senderSession.getId()) ) {
//    			session.sendMessage(message);
//    		}
//            if (!session.getId().equals(senderSession.getId())) {
//                session.sendMessage(message);
//                
//                System.out.println(message);
//            }
//        }
    	if (message.getPayload().equals("update-available-connections")) {
    	    // Generate a list of available connections
    	    List<WebSocketSession> availableSessions = new ArrayList<>(sessions);
    	    availableSessions.remove(senderSession);
    	    if (!availableSessions.isEmpty()) {
    	      // Select a random peer and send the offer signal
    	      int randomIndex = new Random().nextInt(availableSessions.size());
    	      WebSocketSession randomSession = availableSessions.get(randomIndex);
    	      senderSession.sendMessage(new TextMessage(
    	        "{\"type\":\"available-connections\",\"connections\":[{\"offer\":\"" +
    	        randomSession.getId() + "\"}]}"));

    	      // Pair the two peers
    	      availableSessions.remove(randomSession);
    	      availableSessions.remove(senderSession);
    	      sessions.remove(randomSession);
    	      sessions.remove(senderSession);

    	      randomSession.sendMessage(new TextMessage(
    	        "{\"type\":\"available-connections\",\"connections\":[{\"offer\":\"" +
    	        senderSession.getId() + "\"}]}"));
    	      randomSession.sendMessage(new TextMessage("{\"type\":\"offer\"}"));
    	      senderSession.sendMessage(new TextMessage("{\"type\":\"offer\"}"));
    	    } else {
    	      // No available connections, send an empty list
    	      senderSession.sendMessage(new TextMessage("{\"type\":\"available-connections\",\"connections\":[]}"));
    	    }
    	  } else {
    	    // Send the message to the paired session
    	    for (WebSocketSession session : sessions) {
    	      if (!session.getId().equals(senderSession.getId())) {
    	        session.sendMessage(message);
    	      }
    	    }
    	  }
    }
}