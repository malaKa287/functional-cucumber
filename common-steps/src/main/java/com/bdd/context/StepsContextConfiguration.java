package com.bdd.context;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@ComponentScan("com.bdd")
@Import(RestContext.class)
public class StepsContextConfiguration {

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}
}