package dny.apps.tiaw.validation.user;

import dny.apps.tiaw.domain.models.service.UserRegisterServiceModel;

public interface UserValidationService {
	boolean isValid(UserRegisterServiceModel userRegisterServiceModel);
}
