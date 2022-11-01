package com.jkngil.pos.api.users.controller.ui.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jkngil.pos.api.users.service.UsersService;
import com.jkngil.pos.api.users.shared.UserDto;
import com.jkngil.pos.api.users.ui.model.CreateUserRequestModel;
import com.jkngil.pos.api.users.ui.model.CreateUserResponseModel;
import com.jkngil.pos.api.users.ui.model.UpdateUserRequestModel;
import com.jkngil.pos.api.users.ui.model.UserResponseModel;

@RestController
@RequestMapping("/users")
public class UsersController {
	
	@Autowired
	UsersService usersService;
	
	@Autowired
	private Environment env;
	
	@GetMapping("/status/check")
	public String getStatus() {
		return "Working on port " + env.getProperty("local.server.port") + ", with token = " + env.getProperty("token.secret");
	}
	
	@PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<CreateUserResponseModel> createUser(@Valid @RequestBody CreateUserRequestModel userDetails) {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		UserDto userDto = modelMapper.map(userDetails, UserDto.class);
		userDto.setRoles(new HashSet<>(Arrays.asList("ROLE_ADMIN")));
		UserDto createdUser = usersService.createUser(userDto);

		CreateUserResponseModel returnValue = modelMapper.map(createdUser, CreateUserResponseModel.class);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(returnValue);
	}
	
	
	@GetMapping(value="/{userId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
//	@PreAuthorize("principal==#userId")
//	@PostAuthorize("principal==returnObject.getBody().getUserId()")
	public ResponseEntity<UserResponseModel> getUser(@PathVariable("userId") String userId) {
		UserDto userDto = usersService.getUserByUserId(userId);
		UserResponseModel returnValue = new ModelMapper().map(userDto, UserResponseModel.class);
		
		return ResponseEntity.status(HttpStatus.OK).body(returnValue);
	}
	
	@GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<List<UserResponseModel>> getUsers(@RequestParam(value="page", defaultValue="0") int page, @RequestParam(value="limit", defaultValue="25") int limit) {
		List<UserResponseModel> returnValue = new ArrayList<>();
		List<UserDto> users = usersService.getUsers(page, limit);
		for(UserDto userDto: users) {
			ModelMapper modelMapper = new ModelMapper();
			modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
			UserResponseModel user = modelMapper.map(userDto, UserResponseModel.class);
			returnValue.add(user);
		}

		return ResponseEntity.status(HttpStatus.OK).body(returnValue);
	}
	
	@PutMapping(value="/{userId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<UserResponseModel> updateUser(@PathVariable String userId, @Valid @RequestBody UpdateUserRequestModel userDetails) {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		UserDto userDto = modelMapper.map(userDetails, UserDto.class);
		UserDto updatedUser = usersService.updateUser(userId, userDto);

		UserResponseModel returnValue = modelMapper.map(updatedUser, UserResponseModel.class);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(returnValue);
	}
	
	@DeleteMapping(value="/{userId}")
	@PreAuthorize("hasRole('ADMIN')")
	public HttpStatus deleteUser(@PathVariable String userId) {
		usersService.deleteUser(userId);
		return HttpStatus.OK;
	}
}
