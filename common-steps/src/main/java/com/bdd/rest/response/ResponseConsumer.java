package com.bdd.rest.response;

import org.springframework.http.ResponseEntity;

@FunctionalInterface
public interface ResponseConsumer {

	<T> void onResponse(ResponseEntity<T> responseEntity);
}
