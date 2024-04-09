package com.remi;

import static com.remi.LocalConf.PT;
import static com.remi.LocalConf.chatModel;
import static com.remi.LocalConf.embeddingModel;
import static com.remi.LocalConf.embeddingStore;

import java.util.List;

import dev.langchain4j.chain.ConversationalRetrievalChain;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.retriever.EmbeddingStoreRetriever;
import dev.langchain4j.store.embedding.EmbeddingMatch;

public class ChromaSearcher {

	public static MessageWindowChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
	
	public final static ConversationalRetrievalChain SEARCH_CHAIN = ConversationalRetrievalChain.builder()
			.promptTemplate(PT)
			.chatLanguageModel(chatModel)
			.chatMemory(chatMemory)
			.retriever(EmbeddingStoreRetriever.from(embeddingStore, embeddingModel, 4))
			.build();


	public static List<EmbeddingMatch<TextSegment>> search(String query,
			int maxResults) { 
		Embedding queryEmbedding = embeddingModel.embed(query).content();
		return embeddingStore.findRelevant(queryEmbedding, maxResults);
	}

	public static String smartSearch(String question) {
		System.out.println("Answering: " + question);
		
		String answer = SEARCH_CHAIN.execute(question);
		
		ChatMessage message = chatMemory.messages().stream().filter(UserMessage.class::isInstance).reduce((first, second) -> second).orElse(null);
		System.out.println("Detail: " + message.text());
		
		return answer;
	}

}
