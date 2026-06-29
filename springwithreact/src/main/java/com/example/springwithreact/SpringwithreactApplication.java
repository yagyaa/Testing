package com.example.springwithreact;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SpringwithreactApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringwithreactApplication.class, args);
	}

}
