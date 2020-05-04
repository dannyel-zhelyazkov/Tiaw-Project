package dny.apps.tiaw.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import dny.apps.tiaw.domain.entities.User;
import dny.apps.tiaw.domain.models.service.UserServiceModel;
import dny.apps.tiaw.repository.GameAccRepository;
import dny.apps.tiaw.repository.RoleRepository;
import dny.apps.tiaw.repository.UserRepository;

class UserServiceTest {
	private UserRepository userRepository;
	private GameAccRepository gameAccRepository;
	private RoleRepository roleRepository;
	private ModelMapper modelMapper;
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	private UserService userService;
	private RoleService roleService;
	
	@BeforeEach
	void init() {
		@SuppressWarnings("serial")
		Optional<User> user = Optional.of(new User() {
			{
				setId("TEST_ID");
				setUsername("TEST_NAME");
				setEmail("TEST_EMAIL");
				setPassword("TEST_PASS");
			}
		});
		
		@SuppressWarnings("serial")
		User user2 = new User() {
			{
				setId("TEST_ID");
				setUsername("TEST_NAME2");
				setEmail("TEST_EMAIL2");
				setPassword("TEST_PASS2");
			}
		};

		this.userRepository = Mockito.mock(UserRepository.class);
		this.gameAccRepository = Mockito.mock(GameAccRepository.class);
		this.roleRepository = Mockito.mock(RoleRepository.class);
		this.modelMapper = new ModelMapper();
		this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
		this.roleService = new RoleServiceImpl(this.roleRepository, this.modelMapper);
		this.userService = new UserServiceImpl(this.userRepository, this.gameAccRepository, 
				this.roleService, this.modelMapper, this.bCryptPasswordEncoder);
		
		Mockito.when(this.userRepository.findByUsername("TEST_NAME"))
			.thenReturn(user);
		Mockito.when(this.userRepository.saveAndFlush(user2))
			.thenReturn(user2);
	}

	@Test
	void testRegisterUser() {		
		UserServiceModel userServiceModel = this.userService.registerUser(new UserServiceModel());
	}

	@Test
	void testFindUserByUsername() {
		String username = "TEST_NAME";
		UserServiceModel userServiceModel = this.userService.findUserByUsername("TEST_NAME");

		assertEquals(username, userServiceModel.getUsername());
	}

	@Test
	void testEditUserProfile() {

	}

	@Test
	void testFindAllUsers() {

	}

	@Test
	void testSetUserRole() {

	}

}
