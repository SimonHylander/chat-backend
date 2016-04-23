package com.shy.app.websocket.Chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import com.shy.app.websocket.Items;
import com.shy.app.websocket.Chat.model.User;
import com.shy.app.websocket.Chat.service.UserService;

@org.eclipse.jetty.websocket.api.annotations.WebSocket
public class WebSocket {
    private final static HashMap<String, WebSocket> sockets = new HashMap<>();
    private Session session;
    private String sessionId;
    
    UserService userService = new UserService();
    
    private String getNewSessionId() { // unique ID from this class' hash code
        return Integer.toHexString(this.hashCode());
    }
    
    public List<User> getUsers() {
    	List<User> userList = new ArrayList<>();
    	
    	for(int i = 1; i < 3; i++) {
    		User user = new User();
    		user.setId(1);
    		user.setUsername("simon"+i);
    		userList.add(user);
    	}
    	
    	return userList;
    }
    
    @OnWebSocketConnect
    public void onConnect(Session session) {
    	System.out.println("Connected");
        this.session = session; // save session so we can send
        this.sessionId = this.getNewSessionId(); // this unique ID
        WebSocket.sockets.put(this.sessionId, this); // map this unique ID to this connection
    	
        List<String> connections = new ArrayList<>();
        Map<String, String> userMap = new HashMap<>();
        
        List<User> users = new ArrayList<>();
        
        users.forEach(user -> {
        	
        });
        
        
        this.sockets.entrySet()
        	.forEach(socket ->  {
        		//userMap.put(socket.getKey(), );
        	});
        
        //userMap.put(key, value)
        
        
        //webSocket.sendToClient("");
        
        
        /*List<Items> itemsList = itemService.getAllItems();
        this.sendToClient(createClientMessage(itemsList).toString());
        
        // broadcast this new connection (with its unique ID) to all other connected clients
        for (WebSocket webSocket : WebSocket.sockets.values()) {
            if (webSocket == this) {// skip me
                continue; 
            }
            webSocket.sendToClient(createClientMessage(itemsList).toString());
        }*/
    }
    
    public JSONObject createClientMessage(List<Items> itemList)  {
    	JSONObject jsonObject = new JSONObject();
    	JSONArray jsonArray = new JSONArray(itemList);
        jsonObject.put("client_id", this.sessionId);
        jsonObject.put("data", jsonArray);
        return jsonObject;
    }

    @OnWebSocketMessage
    public void onMsg(String message) {
    	JSONObject jsonMessage = new JSONObject(message);
        /*if(jsonMessage.getString("action") != null) {
        	String action = jsonMessage.getString("action");
         	if(action.equals("add")) {
         		String name = jsonMessage.getString("name");
         		String description = jsonMessage.getString("description");
         		
         		Items item = new Items();
         		item.setName(name);
         		item.setDescription(description);
         		
         		try {
         			itemService.saveItem(item);
         		}catch(Exception e) {
         			e.printStackTrace();
         		}
         		
         		List<Items> itemList = itemService.getAllItems();
         		for (WebSocket webSocket : WebSocket.sockets.values()) {
                    webSocket.sendToClient(createClientMessage(itemList).toString());
                }
         	}
        }*/
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        if (WebSocket.sockets.containsKey(this.sessionId)) { // remove connection
            WebSocket.sockets.remove(this.sessionId);
            // broadcast this lost connection to all other connected clients
            for (WebSocket dstSocket : WebSocket.sockets.values()) {
                if (dstSocket == this) {// skip me
                    continue;
                }
                dstSocket.sendToClient(String.format("{\"msg\": \"lostClient\", \"lostClientId\": \"%s\"}",
                        this.sessionId));
            }
        }
    }
    
    private void sendToClient(String str) {
        try {
            this.session.getRemote().sendString(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendError(String err) {
        this.sendToClient(String.format("{\"msg\": \"error\", \"error\": \"%s\"}", err));
    }
}