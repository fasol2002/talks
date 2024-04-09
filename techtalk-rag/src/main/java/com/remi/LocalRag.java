package com.remi;
import java.io.IOException;
import java.nio.file.Paths;

import com.remi.web.Init;

public class LocalRag {

	public static String onNewQuestion(String question) {
		return ChromaSearcher.smartSearch(question);
	}

	public static void insertToChroma() {
		try {
			ChromaInserter.addPath(Paths.get("./data/hobbies.txt"));
			ChromaInserter.addPath(Paths.get("./data/birthday.txt"));
			ChromaInserter.addPath(Paths.get("./data/relationship.txt"));
			ChromaInserter.addPath(Paths.get("./data/otherfact.txt"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException {
		Init.initHttpServer();
	}
}
