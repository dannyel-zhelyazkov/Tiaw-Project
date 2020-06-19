package dny.apps.tiaw.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import dny.apps.tiaw.domain.entities.Card;
import dny.apps.tiaw.domain.entities.Deck;
import dny.apps.tiaw.domain.entities.GameAcc;
import dny.apps.tiaw.domain.entities.Role;
import dny.apps.tiaw.domain.entities.User;
import dny.apps.tiaw.domain.models.service.UserRegisterServiceModel;
import dny.apps.tiaw.domain.models.service.UserServiceModel;
import dny.apps.tiaw.error.user.InvalidUserRegisterException;
import dny.apps.tiaw.error.user.UserNotFoundException;
import dny.apps.tiaw.repository.CardRepository;
import dny.apps.tiaw.repository.DeckRepository;
import dny.apps.tiaw.repository.GameAccRepository;
import dny.apps.tiaw.repository.UserRepository;

class UserServiceTest extends BaseServiceTest {
	
	@Autowired
	private UserService service;
	
	@MockBean
	private UserRepository userRepository;
	@MockBean
	private GameAccRepository gameAccRepository;
	@MockBean
	private CardRepository cardRepository;
	@MockBean
	private DeckRepository deckRepository;
	
	private User user;
	
	@BeforeEach
	public void setUp() {
		Mockito.when(this.userRepository.findByUsername("WRONG_USER"))
			.thenReturn(Optional.empty());
		
		Mockito.when(this.userRepository.findById("WRONG_USER_ID"))
			.thenReturn(Optional.empty());
		
		user = new User();
		user.setAuthorities(new LinkedHashSet<>());
		
		Mockito.when(this.userRepository.findByUsername("USER"))
			.thenReturn(Optional.of(user));
		
		Mockito.when(this.userRepository.findById("TEST_ID"))
			.thenReturn(Optional.of(user));
	}
	
	@Test
	void findByUsername_whenUserDoesNotExist_shoudlThrowUserNotFoundExcepion() {		
		assertThrows(UserNotFoundException.class, () -> 
			this.service.findByUsername("WRONG_USER")
		);
	}
	
	@Test
	void findByUsername_whenUserDoesExist_shouldReturnUser() {		
		UserServiceModel userServiceModel = this.service.findByUsername("USER");
		
		assertEquals(user.getUsername(), userServiceModel.getUsername());
	}

	@Test
	void findAllUsers_whenThereAreThreeUsers_shouldReturnThreeUsers() {
		User user1 = new User();
		User user2 = new User();
		User user3 = new User();
		
		List<User> users = new ArrayList<>();
		users.add(user1);
		users.add(user2);
		users.add(user3);
		
		Mockito.when(this.userRepository.findAll())
			.thenReturn(users);
		
		List<UserServiceModel> userServiceModels = this.service.findAll();
		
		assertEquals(users.size(), userServiceModels.size());
	}
	
	@Test
	void findAllUsersPage_whenThereThreeUsers_whenPageRequestSizeIsOne_shouldReturnPageWithThreeTotal() {
		User user1 = new User();
		User user2 = new User();
		User user3 = new User();
		
		List<User> users = new ArrayList<>();
		users.add(user1);
		users.add(user2);
		users.add(user3);
		
		Page<User> pageUsers = new PageImpl<>(users, PageRequest.of(0, 1), users.size());
		
		Mockito.when(this.userRepository.findAll(PageRequest.of(0, 1)))
			.thenReturn(pageUsers);
		
		Page<UserServiceModel> userServiceModels = this.service.findAll(PageRequest.of(0, 1));
		
		assertEquals(users.size(), userServiceModels.getTotalPages());
	}

	private UserRegisterServiceModel notValidUserRegisterServiceModel() {
		return new UserRegisterServiceModel() {{
			setUsername("Ne");
		}};
	}
	
	private UserRegisterServiceModel validUserRegisterServiceModel() {
		return new UserRegisterServiceModel() {{
			setUsername("USERNAME");
			setEmail("EMAIL@EMAIL.EMAIL");
			setPassword("PASSWORD");
			setConfirmPassword("PASSWORD");
		}};
	}
	
	@Test
	void registerUser_whenUserIsNotValid_shouldThrowInvalidUserRegisterException() {
		UserRegisterServiceModel userRegisterServiceModel = notValidUserRegisterServiceModel();
		
		assertThrows(InvalidUserRegisterException.class, () -> 
			this.service.registerUser(userRegisterServiceModel)
		);
	}
	
	@Test
	void registerUser_whenUserIsValid_shouldRegisterUser() {
		UserRegisterServiceModel userRegisterServiceModel = validUserRegisterServiceModel();
		
		this.service.registerUser(userRegisterServiceModel);
		
		ArgumentCaptor<User> argumentUser = ArgumentCaptor.forClass(User.class);
		ArgumentCaptor<GameAcc> argumentAcc = ArgumentCaptor.forClass(GameAcc.class);
		ArgumentCaptor<Deck> argumentDeck = ArgumentCaptor.forClass(Deck.class);
		ArgumentCaptor<Card> argumentCard = ArgumentCaptor.forClass(Card.class);
		
		Mockito.verify(this.userRepository).saveAndFlush(argumentUser.capture());
		Mockito.verify(this.gameAccRepository).saveAndFlush(argumentAcc.capture());
		Mockito.verify(this.deckRepository).saveAndFlush(argumentDeck.capture());
		Mockito.verify(this.cardRepository, Mockito.times(2)).saveAndFlush(argumentCard.capture());
		
		User user = argumentUser.getValue();
		GameAcc gameAcc = argumentAcc.getValue();
		Deck deck = argumentDeck.getValue();
		List<Card> cards =argumentCard.getAllValues();	
		
		//GameAcc
		assertEquals(gameAcc.getBattlePoints(), 100L);
		assertEquals(gameAcc.getDecks().size(), 1);
		assertEquals(gameAcc.getDecks().get(0).getName(), "StartDeck");
		assertEquals(gameAcc.getDecks().get(0).getCards().size(), 2);
		assertEquals(gameAcc.getCards().size(), 2);
		
		//Deck
		assertEquals(deck.getName(), "StartDeck");
		assertEquals(deck.getCards().size(), 2);
		
		//User
		assertEquals(user.getUsername(), userRegisterServiceModel.getUsername());
		assertEquals(user.getPassword(), userRegisterServiceModel.getPassword());
		assertEquals(user.getEmail(), userRegisterServiceModel.getEmail());
		assertNotNull(user.getGameAcc());
		assertEquals(user.getGameAcc().getBattlePoints(), 100L);
		assertEquals(user.getGameAcc().getDecks().size(), 1);
		assertEquals(user.getGameAcc().getDecks().get(0).getName(), "StartDeck");
		assertEquals(user.getGameAcc().getDecks().get(0).getCards().size(), 2);
		assertEquals(user.getGameAcc().getCards().size(), 2);
		
		//Cards
		assertEquals(cards.get(0).getName(), "Da");
		assertEquals(cards.get(1).getName(), "Dao");
		assertEquals(cards.size(), 2);
	}

	@Test
	void setUserRole_whenUserDoesNotExist_shouldThrowUserNotFoundException() {		
		assertThrows(UserNotFoundException.class, () -> 
			this.service.setUserRole("WRONG_USER_ID", "user")
		);
	}
	
	@Test
	void setUserRole_whenRoleIsUser_shouldAddUserRole() {		
		this.service.setUserRole("TEST_ID", "user");
		
		assertTrue(user.getAuthorities().size() == 1);
		assertTrue(((Role)user.getAuthorities().toArray()[0])
				.getAuthority().equals("ROLE_USER"));
	}
	
	@Test
	void setUserRole_whenRoleIsModerator_shouldAddUserRoleAndModeratorRole() {	
		this.service.setUserRole("TEST_ID", "moderator");
		
		assertTrue(user.getAuthorities().size() == 2);
		assertTrue(((Role)user.getAuthorities().toArray()[0])
				.getAuthority().equals("ROLE_USER"));
		assertTrue(((Role)user.getAuthorities().toArray()[1])
				.getAuthority().equals("ROLE_MODERATOR"));
	}
	
	@Test
	void setUserRole_whenRoleIsAdmin_shouldAddUserRoleAndModeratorRoleAndAdminRole() {	
		this.service.setUserRole("TEST_ID", "admin");
		
		assertTrue(user.getAuthorities().size() == 3);
		assertTrue(((Role)user.getAuthorities().toArray()[0])
				.getAuthority().equals("ROLE_USER"));
		assertTrue(((Role)user.getAuthorities().toArray()[1])
				.getAuthority().equals("ROLE_MODERATOR"));
		assertTrue(((Role)user.getAuthorities().toArray()[2])
				.getAuthority().equals("ROLE_ADMIN"));
	}
}