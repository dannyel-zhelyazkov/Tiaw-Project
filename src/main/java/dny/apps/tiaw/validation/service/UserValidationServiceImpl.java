package dny.apps.tiaw.validation.service;

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
		if(isUsernamePresented(userRegisterServiceModel.getUsername()) ||
				isUsernameLengthIvalid(userRegisterServiceModel.getUsername()) ||
				doPasswordsMatch(userRegisterServiceModel.getPassword(), userRegisterServiceModel.getConfirmPassword()) ||
				isEmailPresented(userRegisterServiceModel.getEmail())) {
			return false;
		}
		
		return true;
	}	
	
	private boolean isUsernamePresented(String username) {
		return this.userRepository.findByUsername(username).isPresent();
	}
	
	private boolean isUsernameLengthIvalid(String username) {
		return username.length() < 3 || username.length() > 18;
	}
	
	private boolean doPasswordsMatch(String password, String confirmPassword) {
		return !password.equals(confirmPassword);
	}
	
	private boolean isEmailPresented(String email) {
		return this.userRepository.findByEmail(email).isPresent();
	}
}
