package dny.apps.tiaw.validation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import dny.apps.tiaw.domain.models.binding.UserSearchBindingModel;
import dny.apps.tiaw.repository.UserRepository;
import dny.apps.tiaw.validation.ValidationConstants;
import dny.apps.tiaw.validation.annotation.Validator;

@Validator
public class UserSearchValidator implements org.springframework.validation.Validator {
	private final UserRepository userRepository;

	@Autowired
	public UserSearchValidator(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public boolean supports(Class<?> aClass) {
		return UserSearchBindingModel.class.equals(aClass);
	}

	@Override
	public void validate(Object target, Errors errors) {
		UserSearchBindingModel userSearchBindingModel = (UserSearchBindingModel) target;

		if(userSearchBindingModel.getUsername().length() == 0) {
			errors.rejectValue("username",
					ValidationConstants.EMPTY_USERNAME,
					ValidationConstants.EMPTY_USERNAME
			);
		}
		
		if (!this.userRepository.findByUsername(userSearchBindingModel.getUsername()).isPresent() && userSearchBindingModel.getUsername().length() > 0) {
			errors.rejectValue("username",
					String.format(ValidationConstants.USERNAME_DOES_NOT_EXISTS, userSearchBindingModel.getUsername()),
					String.format(ValidationConstants.USERNAME_DOES_NOT_EXISTS, userSearchBindingModel.getUsername())
			);
		}
	}
}
