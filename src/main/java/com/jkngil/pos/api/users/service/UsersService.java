package com.jkngil.pos.api.users.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.jkngil.pos.api.users.shared.UserDto;

public interface UsersService extends UserDetailsService{
	UserDto createUser(UserDto userDetails);
	UserDto updateUser(String id, UserDto user);
	void deleteUser(String id);
	UserDto getUserDetailsByEmail(String email);
	UserDto getUserByUserId(String userId);
	List<UserDto> getUsers(int page, int limit);
}
