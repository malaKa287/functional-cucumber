package org.example.someservice.model;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public class User {

	private static final AtomicInteger ID_COUNTER = new AtomicInteger();

	@Setter(AccessLevel.NONE)
	private String id;
	private String firstName;
	private String lastName;
	@Setter(AccessLevel.NONE)
	private Instant createdAt;
	@Nullable
	private Instant updatedAt;

	public User(String firstName, String lastName) {
		this.id = ID_COUNTER.incrementAndGet() + "";
		this.firstName = firstName;
		this.lastName = lastName;
		this.createdAt = Instant.now();
	}

	public User update(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.updatedAt = Instant.now();
		return this;
	}
}
