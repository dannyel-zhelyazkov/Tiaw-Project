package dny.apps.tiaw.domain.models.view;

public class UserProfileViewModel {
	private String usernаme;
	private String email;
	
	public UserProfileViewModel() {}
	
	public String getUsername() {
		return usernаme;
	}
	
	public void setUsername(String usernаme) {
		this.usernаme = usernаme;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
}
