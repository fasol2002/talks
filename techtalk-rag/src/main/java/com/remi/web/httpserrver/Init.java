package com.remi.web.httpserrver;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

public class Init {

	public static void initHttpServer() throws IOException {
		// Create a new HTTP server on port 8011
        HttpServer server = HttpServer.create(new InetSocketAddress(8011), 0);
        server.createContext("/index.html", new IndexHandler());
        server.createContext("/chat.html", new ChatHandler());
        server.createContext("/insert.html", new InsertHandler());
        server.setExecutor(null);
        server.start();
	}

}
