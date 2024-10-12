package org.example.someservice.dto;

import lombok.NonNull;

public record UpdateUserDto(@NonNull String id, String firstName, String lastName) {
}