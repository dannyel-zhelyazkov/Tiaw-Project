package dny.apps.tiaw.domain.models.service;

import java.util.Set;

public class UserRegisterServiceModel {
	private String username;
	private String password;
	private String confirmPasswrod;
	private String email;
	
	public UserRegisterServiceModel() {}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getConfirmPasswrod() {
		return confirmPasswrod;
	}
	public void setConfirmPasswrod(String confirmPasswrod) {
		this.confirmPasswrod = confirmPasswrod;
	}

	public void setAuthorities(Set<RoleServiceModel> findAllRoles) {
		// TODO Auto-generated method stub
		
	}
}
