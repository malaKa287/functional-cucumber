package org.example.someservice.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.Collection;

import org.example.someservice.dto.CreateUserDto;
import org.example.someservice.dto.UpdateUserDto;
import org.example.someservice.dto.UserDto;
import org.example.someservice.mapper.UserMapper;
import org.example.someservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final UserMapper userMapper;

	@GetMapping(produces = APPLICATION_JSON_VALUE)
	ResponseEntity<Collection<UserDto>> findAll() {
		log.info("Finding all users.");
		var result = userService.findAll().stream()
				.map(userMapper::toDto)
				.toList();

		return ResponseEntity.ok(result);
	}

	@GetMapping(value = "{id}/with-headers", produces = APPLICATION_JSON_VALUE)
	ResponseEntity<UserDto> findById(@PathVariable String id, @RequestHeader("header_key") String header) {
		log.info("Finding user with id: {}. Header: {}", id, header);
		var result = userService.findById(id)
				.map(userMapper::toDto);

		return result.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	ResponseEntity<UserDto> create(@RequestBody CreateUserDto createUserDto) {
		log.info("Creating user: {}", createUserDto);
		var result = userService.create(createUserDto);

		return ResponseEntity.ok(userMapper.toDto(result));
	}

	@PutMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	ResponseEntity<UserDto> update(@RequestBody UpdateUserDto updateUserDto) {
		log.info("Updating user: {}", updateUserDto);
		var result = userService.update(updateUserDto);

		return result.map(user -> ResponseEntity.ok(userMapper.toDto(user)))
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@DeleteMapping("{id}")
	ResponseEntity<Void> delete(@PathVariable String id) {
		log.info("Removing user: {}", id);
		var result = userService.delete(id);

		return result
				? ResponseEntity.ok().build()
				: ResponseEntity.notFound().build();
	}
}
