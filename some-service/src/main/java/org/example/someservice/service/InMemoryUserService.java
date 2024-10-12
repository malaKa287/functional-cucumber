package org.example.someservice.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.example.someservice.dto.CreateUserDto;
import org.example.someservice.dto.UpdateUserDto;
import org.example.someservice.model.User;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class InMemoryUserService implements UserService {

	private final List<User> users = new ArrayList<>();

	@PostConstruct
	void init() {
		users.add(new User("John", "Doe"));
		users.add(new User("John", "Update"));
		users.add(new User("John", "Delete"));
	}

	@Override
	public Collection<User> findAll() {
		return users;
	}

	@Override
	public Optional<User> findById(String id) {
		return users.stream()
				.filter(user -> user.getId().equals(id))
				.findFirst();
	}

	@Override
	public User create(CreateUserDto createDto) {
		var user = new User(createDto.firstName(), createDto.lastName());
		users.add(user);
		return user;
	}

	@Override
	public Optional<User> update(UpdateUserDto updateDto) {
		return users.stream()
				.filter(user -> user.getId().equals(updateDto.id()))
				.findAny()
				.map(user -> user.update(updateDto.firstName(), updateDto.lastName()));
	}

	@Override
	public boolean delete(String id) {
		return users.removeIf(user -> user.getId().equals(id));
	}
}
