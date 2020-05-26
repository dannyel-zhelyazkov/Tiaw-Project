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
		if(this.userRepository.findByUsername(userRegisterServiceModel.getUsername()).isPresent() ||
				userRegisterServiceModel.getUsername().length() < 3 || 
				userRegisterServiceModel.getUsername().length() > 18 || 
				!userRegisterServiceModel.getPassword().equals(userRegisterServiceModel.getConfirmPasswrod()) || 
				this.userRepository.findByEmail(userRegisterServiceModel.getEmail()).isPresent()) {
			return false;
		}
				
		return true;
	}

}
