package com.remi.spring;

import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaApi.ChatRequest;
import org.springframework.ai.ollama.api.OllamaApi.ChatResponse;
import org.springframework.ai.ollama.api.OllamaApi.Message;
import org.springframework.ai.ollama.api.OllamaApi.Message.Role;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.queue.CircularFifoQueue;

@Service
public class OllamaService {

	@Value(value ="${spring.ai.ollamaservice.temperature}")
	public Float ollamaServiceTemperature;
	
	@Autowired
	private VectorStore vectorStore;
	@Autowired
	private OllamaApi ollamaApi;

	private List<String> documentsIds = new ArrayList<>();
	private CircularFifoQueue<Message> memory = new CircularFifoQueue<>(LocalConf.CHAT_MEMORY); 

	public Message query(String query) throws Exception {

		System.out.println("========================================");
		System.out.println("Searching for documents in Chroma...");

		List<Document> results = vectorStore.similaritySearch(SearchRequest.query(query)
				.withTopK(4)
				.withSimilarityThreshold(0.33));

		System.out.println("Found documents in Chroma: \n");
		for(var res : results) {
			System.out.println("- " + res);	
		}
	
		String facts = results.stream().map(r -> r.getContent()).collect(Collectors.joining("\n"));
		Message queryMessage = Message.builder(Role.USER).withContent(query).build();
		List<Message> messages = ListUtils.union(new ArrayList<>(memory), 
				List.of(
						Message.builder(Role.SYSTEM)
						.withContent("You are writing a short answer to a question based on a set of facts or our conversation history. The given facts are: \n" + facts + ". The question is:")
						.build(),
						queryMessage)
				);
		
		System.out.println("Asking query to Mistral:");
		for(var msg : messages) {
			System.out.println("- " + msg);	
		}
		
		// Sync request
		var request = ChatRequest.builder("mistral")
				.withStream(false) // not streaming
				.withMessages(messages)
				.withOptions(OllamaOptions.create().withTemperature(ollamaServiceTemperature))
				.build();
		ChatResponse response = ollamaApi.chat(request);
		memory.add(queryMessage);
		memory.add(response.message());
		System.out.println("Answer: " + response);
		System.out.println("========================================");
		return response.message();
	}

	public void insert() throws IOException {
		System.out.println("========================================");
		addPath(Paths.get("./data/hobbies.txt"));
		addPath(Paths.get("./data/birthday.txt"));
		addPath(Paths.get("./data/relationship.txt"));
		addPath(Paths.get("./data/otherfact.txt"));	
		System.out.println("========================================");
	}

	private void addPath(Path path) throws IOException {
		System.out.println("Adding embeddings from " + path);
		vectorStore.add(Files.readAllLines(path, StandardCharsets.UTF_8).stream()
				.map(line -> new Document(line, Map.of("filename", path.getFileName().toString())))
				.map(this::registerDocument)
				.collect(Collectors.toList()));
		System.out.println("done");
	}

	private Document registerDocument(Document d){
		documentsIds.add(d.getId());
		return d;
	}

	public void clearChatMemory() {
		memory.clear();
	}
	
	public void clearChroma() {
		System.out.println("========================================");
		System.out.println("clear started");
		if(documentsIds.isEmpty()) {
			System.out.println("populating document ids list");
			vectorStore.similaritySearch(SearchRequest.query("*").withTopK(200)).stream().forEach(this::registerDocument);
		}

		vectorStore.delete(documentsIds);
		documentsIds.clear();
		System.out.println("clear ended");
		System.out.println("========================================");
	}

}
