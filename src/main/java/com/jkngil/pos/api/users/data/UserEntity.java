package com.jkngil.pos.api.users.data;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.JoinColumn;
@Entity
@Table(name="users")
public class UserEntity implements Serializable {

	private static final long serialVersionUID = 7619793738152242530L;
	@Id
	@GeneratedValue
	private long id;
	@Column(nullable=false, length=16)
	private String firstName;
	@Column(nullable=false, length=16)
	private String lastName;
	@Column(nullable=false, length=50, unique=true)
	private String email;
	@Column(nullable=false, unique=true)
	private String encryptedPassword;
	@Column(nullable=false, unique=true)
	private String userId;
	@ManyToMany(cascade= { CascadeType.PERSIST }, fetch = FetchType.EAGER )
	@JoinTable(name="user_roles", 
			joinColumns=@JoinColumn(name="users_id",referencedColumnName="id"), 
			inverseJoinColumns=@JoinColumn(name="roles_id",referencedColumnName="id"))
	private Collection<RoleEntity> roles;
	
	
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

	public long getId() {
		return id;
	}

	public void setDatabaseId(long id) {
		this.id = id;
	}

	public Collection<RoleEntity> getRoles() {
		return roles;
	}

	public void setRoles(Collection<RoleEntity> roles) {
		this.roles = roles;
	}

	public void setId(long id) {
		this.id = id;
	}

}
