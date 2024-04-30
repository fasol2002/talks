package com.remi.web.httpserrver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

import com.remi.LocalLangchain4jRag;
import com.remi.utils.Utils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ChatHandler implements HttpHandler {
	
	private static ArrayList<String> conversationHistory = new ArrayList<>();
	private static String chatjs;

	public ChatHandler() throws IOException {
		conversationHistory.add("Server: How can I help you ?" );
		chatjs = Utils.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("js/chat.js"));
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		// Set the response headers
		exchange.getResponseHeaders().set("Content-Type", "text/html");
		exchange.sendResponseHeaders(200, 0);

		// Get the output stream from the exchange
		OutputStream outputStream = exchange.getResponseBody();

		// Read input stream to get the question from client
		if("POST".equals(exchange.getRequestMethod())) {
			InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String question = br.readLine();
	
			// Process the question and generate the response
			String answer = processQuestion(question);
			
			// Add the conversation to history
			conversationHistory.add("User: " + question);
			conversationHistory.add("Server: " + answer);
		}

		// Write the HTML response with conversation history
		String response = "<html><head><title>Simple Retrieval-augmented generation</title></head><body>";
		response += "<h1>Welcome to the Simple Retrieval-augmented generation chat</h1>";
		response += "<div id='chat' width='600px'>";
		for (String line : conversationHistory) {
			response += "<p>" + line + "</p>";
		}
		response += "</div>";
		response += "<input type='text' id='question' placeholder='Ask a question...'>";
		response += "<button onclick='sendMessage()'>Answer</button>";
		response += "<script>";
		response += chatjs;
		response += "</script>";
		response += "<hr/>";
		response += "<button onclick='insert()'>Insert to Chroma</button>";
		response += "</body></html>";
		outputStream.write(response.getBytes());

		// Close the output stream
		outputStream.close();
	}

	private String processQuestion(String question) {
		return LocalLangchain4jRag.onNewQuestion(question);
	}
}