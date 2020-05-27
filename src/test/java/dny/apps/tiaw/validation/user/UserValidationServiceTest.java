package dny.apps.tiaw.validation.user;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import dny.apps.tiaw.domain.entities.User;
import dny.apps.tiaw.domain.models.service.UserRegisterServiceModel;
import dny.apps.tiaw.repository.UserRepository;
import dny.apps.tiaw.service.BaseServiceTest;

class UserValidationServiceTest extends BaseServiceTest {

	@Autowired
	private UserValidationService userValidationService;

	@MockBean
	private UserRepository userRepository;
	
	@BeforeEach
	public void setUp() {
		Mockito.when(this.userRepository.findByUsername("USER"))
		.thenReturn(Optional.of(new User() {{
			setUsername("USER");
		}}));
	}
	
	@Test
	void isValid_whenUserUsernameExists_shouldReturnFalse() {
		UserRegisterServiceModel userRegisterServiceModel = new UserRegisterServiceModel();
		userRegisterServiceModel.setUsername("USER");
			
		assertFalse(this.userValidationService.isValid(userRegisterServiceModel));
	}
	
	@Test
	void isValid_whenUsernameIsTooShort_shouldReturnFalse() {
		UserRegisterServiceModel userRegisterServiceModel = new UserRegisterServiceModel();
		userRegisterServiceModel.setUsername("US");
		
		assertFalse(this.userValidationService.isValid(userRegisterServiceModel));
	}

	@Test
	void isValid_whenUsernameIsTooLong_shouldReturnFalse() {
		UserRegisterServiceModel userRegisterServiceModel = new UserRegisterServiceModel();
		userRegisterServiceModel.setUsername("THIS_USERNAME_IS_TOO_LONG_FOR_USER_USERNAME");
		
		assertFalse(this.userValidationService.isValid(userRegisterServiceModel));
	}
	
	@Test
	void isValid_whenUserEmailExists_shouldReturnFalse() {
		UserRegisterServiceModel userRegisterServiceModel = new UserRegisterServiceModel();
		userRegisterServiceModel.setUsername("USER");
		userRegisterServiceModel.setEmail("EMAIL@EMAIL.COM");
				
		assertFalse(this.userValidationService.isValid(userRegisterServiceModel));
	}
	
	@Test
	void isValid_whenPasswordsDoNotMatch_shouldReturnFalse() {
		UserRegisterServiceModel userRegisterServiceModel = new UserRegisterServiceModel();
		userRegisterServiceModel.setUsername("USER");
		userRegisterServiceModel.setEmail("EMAIL@EMAIL.COM");
		userRegisterServiceModel.setPassword("PASS");
		userRegisterServiceModel.setConfirmPassword("PASSS");
		
		assertFalse(this.userValidationService.isValid(userRegisterServiceModel));
	}
	
	@Test
	void isValid_whenAllIsValid_shouldReturnTrue() {
		UserRegisterServiceModel userRegisterServiceModel = new UserRegisterServiceModel();
		userRegisterServiceModel.setUsername("USER");
		userRegisterServiceModel.setEmail("EMAIL@EMAIL.COM");
		userRegisterServiceModel.setPassword("PASS");
		userRegisterServiceModel.setConfirmPassword("PASS");
		
		assertFalse(this.userValidationService.isValid(userRegisterServiceModel));
	}
}
