package com.bdd.rest.request;

import static com.bdd.steps.CommonRestSteps.BODYLESS_METHODS;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.bdd.rest.response.ResponseConsumer;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RequestExecutor {

	private final WebClient webClient;

	public <T> ResponseEntity<T> send(HttpMethod method, RequestParams requestParams, Class<T> responseType,
			ResponseConsumer responseConsumer) {
		var requestSpec = webClient.method(method)
				.uri(requestParams.uri())
				.contentType(MediaType.APPLICATION_JSON)
				.headers(headers -> {
					if (requestParams.headers() != null) {
						headers.addAll(requestParams.headers());
					}
				});
		if (requestParams.body() != null && !BODYLESS_METHODS.contains(method)) {
			requestSpec.bodyValue(requestParams.body());
		}
		return requestSpec.retrieve()
				.toEntity(responseType)
				.doOnNext(responseConsumer::onResponse)
				.block();
	}
}
