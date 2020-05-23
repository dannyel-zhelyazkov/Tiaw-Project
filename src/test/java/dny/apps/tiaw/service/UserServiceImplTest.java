package dny.apps.tiaw.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import dny.apps.tiaw.domain.entities.GameAcc;
import dny.apps.tiaw.domain.entities.Role;
import dny.apps.tiaw.domain.entities.User;
import dny.apps.tiaw.domain.models.service.UserServiceModel;
import dny.apps.tiaw.error.user.UserNotFoundException;
import dny.apps.tiaw.repository.UserRepository;

@SpringBootTest
@RunWith(SpringRunner.class)
class UserServiceImplTest {
	
	@Autowired
	private UserService service;
	
	@MockBean
	private UserRepository mockUserRepository;
	
	@Test
	void testFindUserByUsername() {		
		Optional<User> user = Optional.of(new User() {
			{
				setId("TEST_ID");
				setUsername("TEST_NAME");
				setEmail("TEST_EMAIL");
				setPassword("TEST_PASS");
			}
		});
		
		Mockito.when(this.mockUserRepository.findByUsername("TEST_NAME"))
			.thenReturn(user);
		
		UserServiceModel userServiceModel = this.service.findUserByUsername("TEST_NAME");
		
		Exception nonExistentUsername = assertThrows(UserNotFoundException.class, ()-> {
			this.service.findUserByUsername("WRONG_NAME");
		});

		assertEquals(nonExistentUsername.getMessage(), "User with given username was not found!");
		
		assertEquals(user.get().getUsername(), userServiceModel.getUsername());
	}

	@Test
	void testFindAllUsers() {
		Optional<User> user1 = Optional.of(new User() {
			{
				setId("TEST_ID1");
				setUsername("TEST_NAME1");
				setEmail("TEST_EMAIL1");
				setPassword("TEST_PASS1");
			}
		});
		Optional<User> user2 = Optional.of(new User() {
			{
				setId("TEST_ID2");
				setUsername("TEST_NAME2");
				setEmail("TEST_EMAIL2");
				setPassword("TEST_PASS2");
			}
		});
		Optional<User> user3 = Optional.of(new User() {
			{
				setId("TEST_ID3");
				setUsername("TEST_NAME3");
				setEmail("TEST_EMAIL3");
				setPassword("TEST_PASS3");
			}
		});
		
		List<User> users = new ArrayList<>();
		users.add(user1.get());
		users.add(user2.get());
		users.add(user3.get());
		
		Mockito.when(this.mockUserRepository.findAll())
			.thenReturn(users);
		
		List<UserServiceModel> userServiceModels = this.service.findAllUsers();
		
		assertEquals(users.size(), userServiceModels.size());
		assertEquals(users.get(0).getUsername(), userServiceModels.get(0).getUsername());
		assertEquals(users.get(1).getUsername(), userServiceModels.get(1).getUsername());
		assertEquals(users.get(2).getUsername(), userServiceModels.get(2).getUsername());
	}

	@Test
	void testRegisterUser() {
		Optional<User> user = Optional.of(new User() {
			{
				setId("TEST_ID");
				setUsername("TEST_NAME");
				setEmail("TEST_EMAIL");
				setPassword("TEST_PASS");
				setGameAcc(new GameAcc());
			}
		});
		
		UserServiceModel userServiceModel = new UserServiceModel() {{
			setUsername("TEST_NAME");
			setEmail("TEST_EMAIL");
			setPassword("TEST_PASS");
			setGameAcc(new GameAcc());
		}};
		
		Mockito.when(this.mockUserRepository.saveAndFlush(any()))
			.thenReturn(user.get());
		
		UserServiceModel actualUser = this.service.registerUser(userServiceModel);
		
		assertEquals(user.get().getUsername(), actualUser.getUsername());
	}

	@Test
	void testEditUserProfile() {
		
	}

	@Test
	void testSetUserRole_setAdmin() {
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
