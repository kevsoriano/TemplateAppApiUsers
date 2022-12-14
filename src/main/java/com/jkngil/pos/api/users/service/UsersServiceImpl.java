package com.jkngil.pos.api.users.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.jkngil.pos.api.users.data.AlbumsServiceClient;
import com.jkngil.pos.api.users.data.RoleEntity;
import com.jkngil.pos.api.users.data.RoleRepository;
import com.jkngil.pos.api.users.data.UserEntity;
import com.jkngil.pos.api.users.data.UsersRepository;
import com.jkngil.pos.api.users.security.UserPrincipal;
import com.jkngil.pos.api.users.shared.UserDto;
import com.jkngil.pos.api.users.ui.model.AlbumResponseModel;

import feign.FeignException;

@Service
public class UsersServiceImpl implements UsersService {
	
	@Autowired
	UsersRepository usersRepository;
	
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
//	@Autowired
//	RestTemplate restTemplate;
	
	@Autowired
	Environment env;
	
	@Autowired
	AlbumsServiceClient albumsServiceClient;
	
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public UserDto createUser(UserDto userDetails) {
		// TODO Auto-generated method stub
		userDetails.setUserId(UUID.randomUUID().toString());
		userDetails.setEncryptedPassword(bCryptPasswordEncoder.encode(userDetails.getPassword()));
		
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		
		UserEntity userEntity = modelMapper.map(userDetails, UserEntity.class);
		
		Collection<RoleEntity> roleEntities = new HashSet<>();
		for(String role: userDetails.getRoles()) {
			RoleEntity roleEntity = roleRepository.findByName(role);
			if(roleEntity != null) {
				roleEntities.add(roleEntity);
			}
		}
		
		userEntity.setRoles(roleEntities);
		usersRepository.save(userEntity);
		
		UserDto returnValue = modelMapper.map(userEntity, UserDto.class);
		
		return returnValue;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity userEntity = usersRepository.findByEmail(username);
		if(userEntity==null) throw new UsernameNotFoundException(username);
		
//		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), true, true,true, true, new ArrayList<>());
		return new UserPrincipal(userEntity);
	}

	@Override
	public UserDto getUserDetailsByEmail(String email) {
		UserEntity userEntity = usersRepository.findByEmail(email);
		if(userEntity==null) throw new UsernameNotFoundException(email);
		
		return new ModelMapper().map(userEntity, UserDto.class);
	}

//  Feign client
	@Override
	public UserDto getUserByUserId(String userId) {
		
		UserEntity userEntity = usersRepository.findByUserId(userId);
		if(userEntity == null) throw new UsernameNotFoundException("User not found.");
		
		UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);
		
//		String albumsUrl = String.format(env.getProperty("albums.url"), userId);
//		
//		ResponseEntity<List<AlbumResponseModel>> albumsListResponse = restTemplate.exchange(albumsUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<AlbumResponseModel>>() {});
//		List<AlbumResponseModel> albumsList = albumsListResponse.getBody();
		
		logger.info("Before calling albums microservice");
		
		List<AlbumResponseModel> albumsList = albumsServiceClient.getAlbums(userId);
		
		logger.info("After calling albums microservice");
		
		userDto.setAlbums(albumsList);
		
		return userDto;
	}

	@Override
	public UserDto updateUser(String id, UserDto user) {
		UserEntity userEntity = usersRepository.findByUserId(id);
		if(userEntity == null) throw new UsernameNotFoundException("User not found.");
		
		userEntity.setFirstName(user.getFirstName());
		userEntity.setLastName(user.getLastName());
		
		Collection<RoleEntity> roleEntities = new HashSet<>();
		for(String role: user.getRoles()) {
			RoleEntity roleEntity = roleRepository.findByName(role);
			if(roleEntity != null) {
				roleEntities.add(roleEntity);
			}
		}
		userEntity.setRoles(roleEntities);
		
		UserEntity updatedUserDetails = usersRepository.save(userEntity);
		
		return new ModelMapper().map(updatedUserDetails, UserDto.class);
	}

	@Override
	public void deleteUser(String id) {
		UserEntity userEntity = usersRepository.findByUserId(id);
		if(userEntity == null) throw new UsernameNotFoundException("User not found.");
		
		usersRepository.delete(userEntity);
	}

	@Override
	public List<UserDto> getUsers(int page, int limit) {
		List<UserDto> returnValue = new ArrayList<>();
		
		Pageable pageableRequest = PageRequest.of(page, limit);
		Page<UserEntity> usersPage = usersRepository.findAll(pageableRequest);
		List<UserEntity> users = usersPage.getContent();
		
		for(UserEntity userEntity: users) {
			ModelMapper modelMapper = new ModelMapper();
			modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
			
			UserDto userDto = modelMapper.map(userEntity, UserDto.class);
			returnValue.add(userDto);
		}
		
		return returnValue;
	}
	
//  Rest Template
//	@Override
//	public UserDto getUserByUserId(String userId) {
//		
//		UserEntity userEntity = usersRepository.findByUserId(userId);
//		if(userEntity == null) throw new UsernameNotFoundException("User not found.");
//		
//		UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);
//		
//		String albumsUrl = String.format(env.getProperty("albums.url"), userId);
//		
//		ResponseEntity<List<AlbumResponseModel>> albumsListResponse = restTemplate.exchange(albumsUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<AlbumResponseModel>>() {});
//		List<AlbumResponseModel> albumsList = albumsListResponse.getBody();
//		
//		userDto.setAlbums(albumsList);
//		
//		return userDto;
//	}

}
