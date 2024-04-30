package com.remi.spring;

import org.springframework.ai.chroma.ChromaApi;
import org.springframework.ai.chroma.ChromaApi.CreateCollectionRequest;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.vectorsore.ChromaVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClient.Builder;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class LocalConf {
	
	private String COL = "LocalSpringRag";
	public static  Integer CHAT_MEMORY = 2;
	
	@Value(value ="${spring.ai.ollama.base-url}")
	private String ollamaBaseUrl;
	@Value(value ="${spring.ai.chroma.base-url}")
	private String chromaBaseUrl;
	
	
	
	@Bean
	public RestTemplate restTemplate() {
	   return new RestTemplate();
	}

	@Bean
	public Builder restClientBuilder() {
		Builder restClientBuilder = RestClient.builder().requestFactory(getClientHttpRequestFactory());
		return restClientBuilder;
	}
	
	@Bean
	public ChromaApi chromaApi(RestTemplate restTemplate) {
	   ChromaApi chromaApi = new ChromaApi(chromaBaseUrl, restTemplate);
	   if(chromaApi.getCollection(COL) == null) {
		   chromaApi.createCollection(new CreateCollectionRequest(COL));
	   }
	   return chromaApi;
	}
	
	@Bean
	public VectorStore chromaVectorStore(EmbeddingClient embeddingClient, ChromaApi chromaApi) {
		ChromaVectorStore vectorStore = new ChromaVectorStore(embeddingClient, chromaApi, COL);
		return vectorStore;
	}
	
    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> enableDefaultServlet() {
        return factory -> factory.setRegisterDefaultServlet(true);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public OllamaApi ollamaApi(Builder restClientBuilder) {
    	OllamaApi ollamaApi = new OllamaApi(ollamaBaseUrl, restClientBuilder);
    	return ollamaApi;
    }

	private ClientHttpRequestFactory getClientHttpRequestFactory() {
		SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        clientHttpRequestFactory.setReadTimeout(0);
        clientHttpRequestFactory.setConnectTimeout(0);
        return clientHttpRequestFactory;
	}
}
