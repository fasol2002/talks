package com.remi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;	

@SpringBootApplication
@ComponentScan(basePackages = "com.remi")
public class LocalSpringRag {
	
	public static void main(String[] args) {
		SpringApplication.run(LocalSpringRag.class, args);
	}

}
