package com.bdd.rest.request;

import java.net.URI;

import org.springframework.util.MultiValueMap;

import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.NonNull;

@Builder
public record RequestParams(@NonNull URI uri,
							@Nullable Object body,
							@Nullable String username,
							@Nullable String password,
							@Nullable MultiValueMap<String, String> headers) {

	public static RequestParams from(@NonNull String uri) {
		return RequestParams.builder()
				.uri(URI.create(uri))
				.build();
	}

	public static RequestParams from(@NonNull String uri, @NonNull JsonRequestPayload jsonPayload) {
		return RequestParams.builder()
				.uri(URI.create(uri))
				.body(jsonPayload.getBody().orElse(null))
				.username(jsonPayload.getUsername().orElse(null))
				.password(jsonPayload.getPassword().orElse(null))
				.headers(jsonPayload.getHeaders())
				.build();
	}
}
