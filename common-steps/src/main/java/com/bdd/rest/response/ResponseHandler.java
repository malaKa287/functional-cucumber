package com.bdd.rest.response;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ResponseHandler implements ResponseConsumer {

	private final List<ResponseEntity<?>> responses = new ArrayList<>();

	@Override
	public <T> void onResponse(ResponseEntity<T> responseEntity) {
		responses.add(responseEntity);
	}

	public void clear() {
		responses.clear();
	}

	public ResponseEntity<?> getLastResponse() {
		return responses.getLast();
	}

	public <T> ResponseEntity<T> getLastResponse(Class<T> responseType) {
		return responses.isEmpty()
				? null
				: createCasted(responses.getLast(), responseType);

		// if (responseType.isInstance(lastResponse.getBody())) {
		// 	return ResponseEntity.status(lastResponse.getStatusCode())
		// 			.headers(lastResponse.getHeaders())
		// 			.body(responseType.cast(lastResponse.getBody()));
		// } else {
		// 	throw new ClassCastException("Cannot cast the response body: %s to: %s".formatted() + responseType.getName());
		// }
	}

	private <T> ResponseEntity<T> createCasted(ResponseEntity<?> response, Class<T> responseType) {
		var parameterized = ResponseEntity.status(response.getStatusCode())
				.headers(response.getHeaders());
		return response.getBody() == null
				? parameterized.build()
				: parameterized.body(responseType.cast(response.getBody()));
	}
}
