package com.jkngil.pos.api.users.ui.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class CreateUserRequestModel {
	@NotEmpty(message="First name can not be null or empty")
	@Size(min=2,message="First Name should not be less than 2 characters")
	private String firstName;
	@NotEmpty(message="Last name can not be null or empty")
	@Size(min=2,message="First Name should not be less than 2 characters")
	private String lastName;
	@NotEmpty(message="Email can not be null or empty")
	@Email
	private String email;
	@NotEmpty(message="Password name can not be null or empty")
	@Size(min=8, max=16,message="First Name should must be equal or greather than 8 characters and less than or equal to 16 characters")
	private String password;

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

}
