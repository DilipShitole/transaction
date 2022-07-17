package com.transaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.web.client.RestTemplate;


@SpringBootApplication
@EnableMongoAuditing
public class TransactionApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(TransactionApplication.class, args);
	}
	@Bean
	public RestTemplate getResttemplateBean() {
		return new RestTemplate();
	}
}
