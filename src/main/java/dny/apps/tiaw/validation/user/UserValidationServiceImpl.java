package dny.apps.tiaw.validation.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dny.apps.tiaw.domain.models.service.UserRegisterServiceModel;
import dny.apps.tiaw.repository.UserRepository;

@Service
public class UserValidationServiceImpl implements UserValidationService {

	private final UserRepository userRepository;

	@Autowired
	public UserValidationServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@Override
	public boolean isValid(UserRegisterServiceModel userRegisterServiceModel) {
		return  isUsernamePresented(userRegisterServiceModel.getUsername()) &&
				usernameLength(userRegisterServiceModel.getUsername()) &&
				passwordsMatch(userRegisterServiceModel.getPassword(), userRegisterServiceModel.getConfirmPasswrod()) &&
				isEmailPresented(userRegisterServiceModel.getEmail());
	}	
	
	private boolean isUsernamePresented(String username) {
		return !this.userRepository.findByUsername(username).isPresent();
	}
	
	private boolean usernameLength(String username) {
		return username.length() >=3 && username.length() <= 18;
	}
	
	private boolean passwordsMatch(String password, String confirmPassword) {
		return password.equals(confirmPassword);
	}
	
	private boolean isEmailPresented(String email) {
		return !this.userRepository.findByEmail(email).isPresent();
	}
}
