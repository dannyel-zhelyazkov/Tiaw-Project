package dny.apps.tiaw.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import javax.validation.Validator;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import dny.apps.tiaw.domain.entities.Role;
import dny.apps.tiaw.domain.entities.User;
import dny.apps.tiaw.domain.models.service.UserRegisterServiceModel;
import dny.apps.tiaw.domain.models.service.UserServiceModel;
import dny.apps.tiaw.error.user.InvalidUserRegisterException;
import dny.apps.tiaw.error.user.UserNotFoundException;
import dny.apps.tiaw.repository.UserRepository;

class UserServiceTest extends BaseServiceTest {
	
	@Autowired
	private UserService service;
	@Autowired
	private Validator validator;
	
	@MockBean
	private UserRepository mockUserRepository;
	
	@Test
	void findByUsername_whenUserDoesNotExist_shoudlThrowUserNotFoundExcepion() {
		String username = "USERNAME";
		
		Mockito.when(this.mockUserRepository.findByUsername(username))
			.thenReturn(Optional.empty());
		
		assertThrows(UserNotFoundException.class, () -> this.service.findByUsername(username));
	}
	
	@Test
	void findByUsername_whenUserDoesExist_shouldReturnUser() {
		String username = "USERNAME";
		
		User user = new User();
		user.setUsername(username);
		
		Mockito.when(this.mockUserRepository.findByUsername(username))
			.thenReturn(Optional.of(user));
		
		UserServiceModel userServiceModel = this.service.findByUsername(username);
		
		assertEquals(user.getUsername(), userServiceModel.getUsername());
	}

	@Test
	void findAllUsers_whenThereAreUsers_shouldReturnAllUsers() {
		User user1 = new User();
		User user2 = new User();
		User user3 = new User();
		
		List<User> users = new ArrayList<>();
		users.add(user1);
		users.add(user2);
		users.add(user3);
		
		Mockito.when(this.mockUserRepository.findAll())
			.thenReturn(users);
		
		List<UserServiceModel> userServiceModels = this.service.findAllUsers();
		
		assertEquals(users.size(), userServiceModels.size());
	}

	@Test
	void registerUser_whenUserIsNotValid_shouldThrowInvalidUserRegisterException() {
		UserRegisterServiceModel userRegisterServiceModel = new UserRegisterServiceModel();
		Mockito.when(this.validator.validate(userRegisterServiceModel).isEmpty())
			.thenReturn(false);
		
		assertThrows(InvalidUserRegisterException.class, () -> this.service.registerUser(userRegisterServiceModel));
	}

	@Test
	void testEditUserProfile() {
		
	}

	@Test
	void testSetUserRole() {
		Optional<User> user = Optional.of(new User() {
			{
				setId("TEST_ID");
				setUsername("TEST_NAME");
				setEmail("TEST_EMAIL");
				setPassword("TEST_PASS");
				setAuthorities(new LinkedHashSet<>());
			}
		});

		Mockito.when(this.mockUserRepository.findById("TEST_ID"))
			.thenReturn(user);
		
		this.service.setUserRole("TEST_ID", "admin");
		
		assertTrue(user.get().getAuthorities().size() == 3);
		assertTrue(((Role)user.get().getAuthorities().toArray()[0])
				.getAuthority().equals("ROLE_USER"));
		assertTrue(((Role)user.get().getAuthorities().toArray()[1])
				.getAuthority().equals("ROLE_MODERATOR"));
		assertTrue(((Role)user.get().getAuthorities().toArray()[2])
				.getAuthority().equals("ROLE_ADMIN"));
		
		this.service.setUserRole("TEST_ID", "moderator");
		
		assertTrue(user.get().getAuthorities().size() == 2);
		assertTrue(((Role)user.get().getAuthorities().toArray()[0])
				.getAuthority().equals("ROLE_USER"));
		assertTrue(((Role)user.get().getAuthorities().toArray()[1])
				.getAuthority().equals("ROLE_MODERATOR"));
		
		this.service.setUserRole("TEST_ID", "user");
		
		assertTrue(user.get().getAuthorities().size() == 1);
		assertTrue(((Role)user.get().getAuthorities().toArray()[0])
				.getAuthority().equals("ROLE_USER"));
	}
}