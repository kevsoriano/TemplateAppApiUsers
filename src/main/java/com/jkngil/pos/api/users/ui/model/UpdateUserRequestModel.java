package com.jkngil.pos.api.users.ui.model;

import java.util.Collection;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class UpdateUserRequestModel {
	@NotEmpty(message="First name can not be null or empty")
	@Size(min=2,message="First Name should not be less than 2 characters")
	private String firstName;
	@NotEmpty(message="First name can not be null or empty")
	@Size(min=2,message="First Name should not be less than 2 characters")
	private String lastName;
	@NotEmpty(message="First name can not be null or empty")
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

	public Collection<String> getRoles() {
		return roles;
	}

	public void setRoles(Collection<String> roles) {
		this.roles = roles;
	}
}
