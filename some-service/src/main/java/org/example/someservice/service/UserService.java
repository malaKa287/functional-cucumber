package org.example.someservice.service;

import java.util.Collection;
import java.util.Optional;

import org.example.someservice.dto.CreateUserDto;
import org.example.someservice.dto.UpdateUserDto;
import org.example.someservice.model.User;

public interface UserService {

	Collection<User> findAll();

	Optional<User> findById(String id);

	User create(CreateUserDto createUserDto);

	Optional<User> update(UpdateUserDto updateUserDto);

	boolean delete(String id);
}
