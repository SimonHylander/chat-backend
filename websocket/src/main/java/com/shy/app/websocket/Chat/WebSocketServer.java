package com.shy.app.websocket.Chat;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class WebSocketServer {
	public static void main(String[] args) {
		Server server = new Server(8080);
		WebSocketHandler wsHandler = new WebSocketHandler() {
			@Override
			public void configure(WebSocketServletFactory factory) {
				factory.register(WebSocket.class);
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