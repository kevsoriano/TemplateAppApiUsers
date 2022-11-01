package com.jkngil.pos.api.users.shared;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.jkngil.pos.api.users.data.RoleEntity;
import com.jkngil.pos.api.users.ui.model.AlbumResponseModel;

public class UserDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6123087435988733660L;
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private String encryptedPassword;
	private String userId;
	private List<AlbumResponseModel> albums;
	private Collection<String> roles;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEncryptedPassword() {
		return encryptedPassword;
	}

	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Collection<String> getRoles() {
		return roles;
	}

	public void setRoles(Collection<String> roles) {
		this.roles = roles;
	}

	public List<AlbumResponseModel> getAlbums() {
		return albums;
	}

	public void setAlbums(List<AlbumResponseModel> albums) {
		this.albums = albums;
	}

}
