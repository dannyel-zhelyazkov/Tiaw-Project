package dny.apps.tiaw.domain.models.binding;

public class UserRegisterBindingModel {
	private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private boolean valid;

    public UserRegisterBindingModel() {
    }

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
    public String getConfirmPassword() {
        return confirmPassword;
    }
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
	public boolean getValid() {
		valid = password != null && confirmPassword != null && password.equals(confirmPassword);
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
}
