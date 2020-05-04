package dny.apps.tiaw.domain.models.binding;

import javax.validation.constraints.*;

public class UserRegisterBindingModel {
	private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private boolean valid;

    public UserRegisterBindingModel() {
    }

    @Size(min = 3, max = 18, message = "Username must be between 6 and 18 characters")
    @NotNull
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    @NotEmpty(message = "Password must be not empty")
    @NotNull
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    @NotEmpty(message = "Password must be not empty")
    @NotNull
    public String getConfirmPassword() {
        return confirmPassword;
    }
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    @Email(message = "Invalid email")
    @NotNull
    @NotEmpty(message = "Email must be not empty")
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    @AssertTrue(message = "Passwords do not match")
	public boolean getValid() {
		valid = password != null && confirmPassword != null && password.equals(confirmPassword);
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
}
