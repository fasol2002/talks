package com.remi.web;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class IndexHandler implements HttpHandler {
	

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		// Set the response headers
		exchange.getResponseHeaders().set("Content-Type", "text/html");
		exchange.sendResponseHeaders(200, 0);

		// Get the output stream from the exchange
		OutputStream outputStream = exchange.getResponseBody();

		// Write the HTML response with conversation history
		String response = "<html><head><title>Simple Retrieval-augmented generation</title></head><body>";
		response += "<h1>Welcome to the Simple Retrieval-augmented generation chat</h1>";
		response += "<a href='/chat.html'>Chat</a>";
		response += "</body></html>";
		outputStream.write(response.getBytes());

		// Close the output stream
		outputStream.close();
	}

}