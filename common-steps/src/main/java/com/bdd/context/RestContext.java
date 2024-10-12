package com.bdd.context;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;

@EnableWebFlux
@Configuration
public class RestContext {

	private static final Integer TIMEOUT_MS = 10_000;

	@Bean
	public WebClient webClient() {
		var httpClient = HttpClient.create()
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT_MS)
				.responseTimeout(Duration.ofMillis(TIMEOUT_MS))
				.doOnConnected(conn -> conn
						.addHandlerLast(new ReadTimeoutHandler(TIMEOUT_MS, TimeUnit.MILLISECONDS))
						.addHandlerLast(new WriteTimeoutHandler(TIMEOUT_MS, TimeUnit.MILLISECONDS)));

		return WebClient.builder()
				.clientConnector(new ReactorClientHttpConnector(httpClient))
				.build();
	}
}
