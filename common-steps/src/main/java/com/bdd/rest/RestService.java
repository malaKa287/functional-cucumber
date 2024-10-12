package com.bdd.rest;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.bdd.rest.request.RequestExecutor;
import com.bdd.rest.request.RequestParams;
import com.bdd.rest.response.ResponseConsumer;
import com.bdd.rest.response.ResponseHandler;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RestService {

	private final RequestExecutor executor;
	private final ResponseHandler responseHandler;

	public void clearResponses() {
		responseHandler.clear();
	}

	public ResponseEntity<String> getLastResponse() {
		return getLastResponse(String.class);
	}

	public <T> ResponseEntity<T> getLastResponse(Class<T> responseType) {
		return responseHandler.getLastResponse(responseType);
	}

	public void send(HttpMethod method, RequestParams requestParams) {
		send(method, requestParams, String.class, responseHandler);
	}

	public <T> ResponseEntity<T> send(HttpMethod method, RequestParams requestParams, Class<T> responseType,
			ResponseConsumer responseConsumer) {
		return executor.send(method, requestParams, responseType, responseConsumer);
	}
}
