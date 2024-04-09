package com.remi;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
//import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;
import static java.time.Duration.ofSeconds;

public class LocalConf {
    
//	public final static PromptTemplate PT = PromptTemplate.from("""
//			You are helping a software engineer to analyze files, he will ask question about the files.
//			Avoid unnecessary sentence, for instance do not try to open the discussion at the end, if he has more question he will ask them.
//			If you have to produce code to answer a question do it in Bash Shell or Java.
//			For each affirmation that you do, add a reference from the file.
//			Answer directly to this question: {{question}}
//			Base your answer on the following file:
//			{{information}}""");

	private static final String CHOMA_COLLECTION_NAME = "test-remi-collection";
	private static final String OLLAMA_MODEL_NAME = "mistral";
	// seconds
	private static final int TIMEOUT = 5 * 60;
	
	private static final String CHROMA_API_BASE_URL = "http://chroma:8000/";
	private static final String OLLAMA_API_BASE_URL = "http://ollama:11434/";

	public final static PromptTemplate PT = PromptTemplate.from("""
			You are a personal assistant. Answer to this question concisely: {{question}}
			Base your answer on the following information, omit piece of information that you deem unnecessary:
			{{information}}""");
	
    public static final EmbeddingStore<TextSegment> embeddingStore =
            ChromaEmbeddingStore.builder()
                    .baseUrl(CHROMA_API_BASE_URL)
                    .collectionName(CHOMA_COLLECTION_NAME)
                    .build();

    public static final EmbeddingModel embeddingModel =
    		OllamaEmbeddingModel.builder()
    				.baseUrl(OLLAMA_API_BASE_URL)
    				.timeout(ofSeconds(TIMEOUT))
                    .modelName(OLLAMA_MODEL_NAME)
                    .build();
    
    public static final ChatLanguageModel chatModel = OllamaChatModel.builder()
            .baseUrl(OLLAMA_API_BASE_URL)
            .timeout(ofSeconds(TIMEOUT))
            .modelName(OLLAMA_MODEL_NAME)
            .build();
}