package com.remi.web.httpserrver;

import java.io.IOException;
import java.io.OutputStream;

import com.remi.LocalLangchain4jRag;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class InsertHandler implements HttpHandler {
	

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		// Set the response headers
		exchange.getResponseHeaders().set("Content-Type", "text/html");
		exchange.sendResponseHeaders(200, 0);

		LocalLangchain4jRag.insertToChroma();
		
		// Get the output stream from the exchange
		OutputStream outputStream = exchange.getResponseBody();

		// Write the HTML response with conversation history
		String response = "<html><head><title>Simple Retrieval-augmented generation</title></head><body>";
		response += "<h1>Inserted</h1>";
		response += "</body></html>";
		outputStream.write(response.getBytes());

		// Close the output stream
		outputStream.close();
	}

}