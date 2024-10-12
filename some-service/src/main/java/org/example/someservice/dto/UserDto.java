package org.example.someservice.dto;

import java.time.Instant;

public record UserDto(String id,
					  String firstName,
					  String lastName,
					  Instant createdAt,
					  String updatedAt) {
}