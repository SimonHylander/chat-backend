package com.shy.app.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONArray;
import org.json.JSONObject;

import com.shy.app.websocket.services.ItemService;

@WebSocket
public class ItemWebSocket {
    private final static HashMap<String, ItemWebSocket> sockets = new HashMap<>();
    private Session session;
    private String myUniqueId;
    private ItemService itemService = new ItemService();
    
    private String getMyUniqueId() { // unique ID from this class' hash code
        return Integer.toHexString(this.hashCode());
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
    	System.out.println("Connected");
        this.session = session; // save session so we can send
        this.myUniqueId = this.getMyUniqueId(); // this unique ID
        ItemWebSocket.sockets.put(this.myUniqueId, this); // map this unique ID to this connection

        List<Items> itemsList = itemService.getAllItems();
        this.sendToClient(createClientMessage(itemsList).toString());
        
        // broadcast this new connection (with its unique ID) to all other connected clients
        for (ItemWebSocket webSocket : ItemWebSocket.sockets.values()) {
            if (webSocket == this) {// skip me
                continue; 
            }
            webSocket.sendToClient(createClientMessage(itemsList).toString());
        }
    }
    
    public JSONObject createClientMessage(List<Items> itemList)  {
    	JSONObject jsonObject = new JSONObject();
    	JSONArray jsonArray = new JSONArray(itemList);
        jsonObject.put("client_id", this.myUniqueId);
        jsonObject.put("data", jsonArray);
        return jsonObject;
    }

    @OnWebSocketMessage
    public void onMsg(String message) {
    	JSONObject jsonMessage = new JSONObject(message);
        if(jsonMessage.getString("action") != null) {
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
         		for (ItemWebSocket webSocket : ItemWebSocket.sockets.values()) {
                    webSocket.sendToClient(createClientMessage(itemList).toString());
                }
         	}
        }
    	
        /*
         * process message here with whatever JSON library or protocol you like
         * to get the destination unique ID from the client and the actual message
         * to be sent (not shown). also, make sure to escape the message string
         * for further JSON inclusion. 
         */
        /*String destUniqueId = ...;
        String escapedMessage = ...;

        // is the destination client connected?
        if (!MyWebSocket.sockets.containsKey(destUniqueId)) {
            this.sendError(String.format("destination client %s does not exist", destUniqueId));
            return;
        }

        // send message to destination client
        this.sendClient(
    		String.format("{\"msg\": \"message\", \"destId\": \"%s\", \"message\": \"%s\"}", destUniqueId, escapedMessage)
        );*/
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        if (ItemWebSocket.sockets.containsKey(this.myUniqueId)) { // remove connection
            ItemWebSocket.sockets.remove(this.myUniqueId);
            // broadcast this lost connection to all other connected clients
            for (ItemWebSocket dstSocket : ItemWebSocket.sockets.values()) {
                if (dstSocket == this) {// skip me
                    continue;
                }
                dstSocket.sendToClient(String.format("{\"msg\": \"lostClient\", \"lostClientId\": \"%s\"}",
                        this.myUniqueId));
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