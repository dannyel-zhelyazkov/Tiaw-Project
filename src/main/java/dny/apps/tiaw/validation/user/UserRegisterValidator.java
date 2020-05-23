package dny.apps.tiaw.validation.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import dny.apps.tiaw.domain.models.binding.UserRegisterBindingModel;
import dny.apps.tiaw.repository.UserRepository;
import dny.apps.tiaw.validation.ValidationConstants;
import dny.apps.tiaw.validation.annotation.Validator;

@Validator
public class UserRegisterValidator implements org.springframework.validation.Validator {
	
	private final UserRepository userRepository;

    @Autowired
    public UserRegisterValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


	@Override
	public boolean supports(Class<?> clazz) {
		 return UserRegisterBindingModel.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		UserRegisterBindingModel userRegisterBindingModel = (UserRegisterBindingModel) target;

        if (this.userRepository.findByUsername(userRegisterBindingModel.getUsername()).isPresent()) {
            errors.rejectValue(
                    "username",
                    String.format(ValidationConstants.USERNAME_ALREADY_EXISTS, userRegisterBindingModel.getUsername())
            );
        }

        if (userRegisterBindingModel.getUsername().length() < 3 || userRegisterBindingModel.getUsername().length() > 18) {
            errors.rejectValue(
                    "username",
                    ValidationConstants.USERNAME_LENGTH,
                    ValidationConstants.USERNAME_LENGTH
            );
        }

        if (!userRegisterBindingModel.getPassword().equals(userRegisterBindingModel.getConfirmPassword())) {
            errors.rejectValue(
                    "password",
                    ValidationConstants.PASSWORDS_DO_NOT_MATCH,
                    ValidationConstants.PASSWORDS_DO_NOT_MATCH
            );
        }

        if (this.userRepository.findByEmail(userRegisterBindingModel.getEmail()).isPresent()) {
            errors.rejectValue(
                    "email",
                    String.format(ValidationConstants.EMAIL_ALREADY_EXISTS, userRegisterBindingModel.getEmail()),
                    String.format(ValidationConstants.EMAIL_ALREADY_EXISTS, userRegisterBindingModel.getEmail())
            );
        }
	}

}
