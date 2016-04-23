package com.shy.app.websocket;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class ItemWebSocketServer {
	public static void main(String[] args) {
		Server server = new Server(8080);
		WebSocketHandler wsHandler = new WebSocketHandler() {
			@Override
			public void configure(WebSocketServletFactory factory) {
				factory.register(ItemWebSocket.class);
			}
		};
		
		try {
			server.setHandler(wsHandler);
			server.start();
			server.join();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}