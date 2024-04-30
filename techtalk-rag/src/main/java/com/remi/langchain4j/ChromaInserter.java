package com.remi.langchain4j;
import static com.remi.langchain4j.LocalConf.embeddingModel;
import static com.remi.langchain4j.LocalConf.embeddingStore;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentBySentenceSplitter;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;

public class ChromaInserter {


    public static void addDocuments(String text) {
        TextSegment segment1 = TextSegment.from(text, new Metadata());
        Embedding embedding1 = embeddingModel.embed(segment1).content();
        embeddingStore.add(embedding1, segment1);
    }

    public static void addDocuments(String text, Metadata metadata) {
        TextSegment segment1 = TextSegment.from(text, metadata);
        Embedding embedding1 = embeddingModel.embed(segment1).content();
        embeddingStore.add(embedding1, segment1);
    }
    
	public static void addPath(Path documentPath) throws InterruptedException {
        DocumentParser documentParser = new TextDocumentParser();
        Document document = FileSystemDocumentLoader.loadDocument(documentPath, documentParser);

        System.out.println("-------------------------");
        System.out.println("loaded - " + documentPath);
        
        //DocumentSplitter splitter = DocumentSplitters.recursive(50, 5);
        DocumentBySentenceSplitter splitter = new DocumentBySentenceSplitter(40, 5);
        List<TextSegment> segments = splitter.split(document);
        
        System.out.println("segmented - " + segments.size() + " for " + documentPath);
       
        System.out.println("embeddings - queued = " + segments.size());
        
        AtomicInteger count = new AtomicInteger(0);
        Map<TextSegment, Embedding> embeddingsMap = Collections.synchronizedMap(new HashMap<>());
        
        for(TextSegment seg : segments) {
        		embeddingsMap.put(seg, embeddingModel.embed(seg).content());
        		System.out.println("embeddings computed - " + count.incrementAndGet() + "/" + segments.size());
        }      

        System.out.println("embedded - " + documentPath);
        
        List<Embedding> embeddings = new ArrayList<>();
        segments.forEach(s -> embeddings.add(embeddingsMap.get(s)));
        
        embeddingStore.addAll(embeddings, segments);
        
        System.out.println("stored - " + documentPath);
    }
}