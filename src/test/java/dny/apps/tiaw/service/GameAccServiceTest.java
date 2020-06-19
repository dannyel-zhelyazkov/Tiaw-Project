package dny.apps.tiaw.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import dny.apps.tiaw.domain.entities.Card;
import dny.apps.tiaw.domain.entities.Deck;
import dny.apps.tiaw.domain.entities.Fight;
import dny.apps.tiaw.domain.entities.GameAcc;
import dny.apps.tiaw.domain.entities.User;
import dny.apps.tiaw.domain.models.service.GameAccServiceModel;
import dny.apps.tiaw.error.card.CardNotFoundException;
import dny.apps.tiaw.error.deck.DeckNotFoundException;
import dny.apps.tiaw.error.user.UserNotFoundException;
import dny.apps.tiaw.repository.CardRepository;
import dny.apps.tiaw.repository.DeckRepository;
import dny.apps.tiaw.repository.FightRepository;
import dny.apps.tiaw.repository.GameAccRepository;
import dny.apps.tiaw.repository.UserRepository;

class GameAccServiceTest extends BaseServiceTest {

	@Autowired
	private GameAccService service;
	
	@MockBean
	private GameAccRepository gameAccRepository;
	@MockBean
	private UserRepository userRepository;
	@MockBean
	private CardRepository cardRepository;
	@MockBean
	private DeckRepository deckRepository;
	@MockBean
	private FightRepository fightRepository;
	
	private User user;
	
	@BeforeEach
	public void setUp() {
		Mockito.when(this.userRepository.findByUsername("WRONG_USER"))
			.thenReturn(Optional.empty());
		
		Mockito.when(this.userRepository.findById("WRONG_USER_ID"))
			.thenReturn(Optional.empty());
		
		this.user = new User();
		this.user.setUsername("USER");
		this.user.setGameAcc(new GameAcc());
		this.user.getGameAcc().setAttackTickets(0);
		this.user.getGameAcc().setGold(300L);
		this.user.getGameAcc().setCards(new ArrayList<>());
		
		Card card = new Card();
		card.setName("CARD_NAME");
		
		this.user.getGameAcc().getCards().add(card);
		
		Mockito.when(this.userRepository.findByUsername("USER"))
			.thenReturn(Optional.of(user));
		
		Mockito.when(this.userRepository.findById("TEST_ID"))
			.thenReturn(Optional.of(user));
	}
	
	@Test
	void findByUser_whenUserDoesNotExist_shouldThrowUserNotFoudnException() {
		assertThrows(UserNotFoundException.class, () -> 
			this.service.findByUser("WRONG_USER")
		);
	}
	
	@Test
	void findByUser_whenUserExists_shouldReturnGameAcc() {		
		GameAccServiceModel gameAccServiceModel  = this.service.findByUser("USER");
		
		assertEquals(user.getUsername(), gameAccServiceModel.getUsername());
	}

	@Test
	void findAll() {
		User user1 = new User() {{
			setGameAcc(new GameAcc() {{
				setBattlePoints(3040L);
			}});
		}};
		User user2 = new User() {{
			setGameAcc(new GameAcc() {{
				setBattlePoints(3050L);
			}});
		}};
		User user3 = new User() {{
			setGameAcc(new GameAcc() {{
				setBattlePoints(3060L);
			}});
		}};
		
		List<User> users = new ArrayList<>();
		users.add(user1);
		users.add(user2);
		users.add(user3);
		
		Mockito.when(this.userRepository.findAll())
			.thenReturn(users);
		
		List<GameAccServiceModel> gameAccServiceModels = this.service.findAll();
		
		assertEquals(users.size(), gameAccServiceModels.size());
	}

	@Test
	void findAllFightGameAccs_whenOneGameAccDoNotHaveDefenseDeck_shouldReturnOneFightGameAcc() {
		User user1 = new User() {{
			setGameAcc(new GameAcc() {{
				setDefenseDeck(new Deck());
				setBattlePoints(3040L);
			}});
		}};
		User user2 = new User() {{
			setGameAcc(new GameAcc() {{
				setDefenseDeck(new Deck());
				setBattlePoints(3040L);
			}});
		}};
		
		List<GameAcc> usersAcc = new ArrayList<>();
		usersAcc.add(user1.getGameAcc());
		usersAcc.add(user2.getGameAcc());
		
		Page<GameAcc> pageUsers = new PageImpl<>(usersAcc, PageRequest.of(0, 1), usersAcc.size());
		
		Mockito.when(this.gameAccRepository.findAllByDefenseDeckNotNull(PageRequest.of(0, 1)))
			.thenReturn(pageUsers);
	
		Page<GameAccServiceModel> gameAccServiceModels = this.service.findAllGameAccFight(PageRequest.of(0, 1));
		
		assertEquals(gameAccServiceModels.getTotalPages(), 2);
	}

	@Test
	void buyCardById_whenUserDoesNotExist_shouldThrowUserNotFoundException() {
		assertThrows(UserNotFoundException.class, () ->
			this.service.buyCardById("CARD_ID", "WRONG_USER")
		);
	}
	
	@Test
	void buyCardById_whenCardDoesNotExist_shouldThrowCardNotFoundException() {		
		Mockito.when(this.cardRepository.findById("WRONG_CARD_ID"))
			.thenReturn(Optional.empty());
		
		assertThrows(CardNotFoundException.class, () ->
			this.service.buyCardById("WRONG_CARD_ID", "USER")
		);
	}
	
	@Test
	void buyCardById() {		
		Mockito.when(this.gameAccRepository.saveAndFlush(any()))
			.thenReturn(user.getGameAcc());
		
		Mockito.when(this.cardRepository.findById("CARD_ID"))
			.thenReturn(Optional.of(new Card() {{
				setPrice(10);
			}}));
		
		this.service.buyCardById("CARD_ID", "USER");
		
		assertEquals(user.getGameAcc().getCards().size(), 2);
	}
	
	@Test
	void buyCardByName_whenUserDoesNotExist_shouldThrowUserNotFoundException() {
		assertThrows(UserNotFoundException.class, () ->
			this.service.buyCardByName("CARD_NAME", "WRONG_USER")
		);
	}
	@Test
	void buyCardByName_whenCardDoesNotExist_shouldThrowCardNotFoundException() {		
		Mockito.when(this.cardRepository.findByName("WRONG_CARD_NAME"))
			.thenReturn(Optional.empty());
		
		assertThrows(CardNotFoundException.class, () ->
			this.service.buyCardByName("WRONG_CARD_NAME", "USER")
		);
	}
	
	@Test
	void buyCardByName() {
		Mockito.when(this.gameAccRepository.saveAndFlush(any()))
			.thenReturn(user.getGameAcc());
		
		Mockito.when(this.cardRepository.findByName("CARD_NAME"))
			.thenReturn(Optional.of(new Card() {{
				setPrice(10);
			}}));
		
		this.service.buyCardByName("CARD_NAME", "USER");
		
		assertEquals(user.getGameAcc().getCards().size(), 2);
		assertEquals(user.getGameAcc().getCards().get(1).getPrice(), 10);
	}
	
	@Test
	void setDefenseDeck_whenUserDoesNotExist_shouldThrowUserNotFoundException() {		
		assertThrows(UserNotFoundException.class, () ->
			this.service.setDefenseDeck("DECK_ID", "WRONG_USER")
		);
	}
	
	@Test
	void setDefenseDeck_whenUserDeckDoesNotExist_shouldThrowDeckNotFoundException() {		
		Mockito.when(this.deckRepository.findById("WRONG_ID"))
		.thenReturn(Optional.empty());
		
		assertThrows(DeckNotFoundException.class, () ->
			this.service.setDefenseDeck("WRONG_ID", "USER")
		);
	}

	@Test
	void setDefenseDeck() {	
		Mockito.when(this.gameAccRepository.saveAndFlush(any()))
			.thenReturn(user.getGameAcc());
		
		Mockito.when(this.deckRepository.saveAndFlush(any()))
			.thenReturn(new Deck());
		
		Mockito.when(this.deckRepository.findById("DECK_ID"))
			.thenReturn(Optional.of(new Deck()));
		
		this.service.setDefenseDeck("DECK_ID", "USER");
		
		assertNotNull(user.getGameAcc().getDefenseDeck());
	}

	@Test
	void setAttackDeck_whenUserDoesNotExist_shouldThrowUserNotFoundException() {
		assertThrows(UserNotFoundException.class, () ->
			this.service.setAttackDeck("DECK_ID", "WRONG_USER")
		);
	}
	
	@Test
	void setAttackDeck_whenUserDeckDoesNotExist_shouldThrowDeckNotFoundException() {		
		Mockito.when(this.deckRepository.findById("WRONG_ID"))
		.thenReturn(Optional.empty());
		
		assertThrows(DeckNotFoundException.class, () ->
			this.service.setAttackDeck("WRONG_ID", "USER")
		);
	}

	@Test
	void setAttackDeck() {
		Mockito.when(this.gameAccRepository.saveAndFlush(any()))
			.thenReturn(user.getGameAcc());
		
		Mockito.when(this.deckRepository.saveAndFlush(any()))
			.thenReturn(new Deck());
		
		Mockito.when(this.deckRepository.findById("DECK_ID"))
			.thenReturn(Optional.of(new Deck()));
		
		this.service.setDefenseDeck("DECK_ID", "USER");
		
		assertNotNull(user.getGameAcc().getDefenseDeck());
	}
	
	@Test
	void wonFight_whenUserDoesNotExist_shouldThrowUserNotFoundException() {		
		assertThrows(UserNotFoundException.class, () ->
			this.service.wonFight("WRONG_USER", "USER", "1", "1")
		);
	}
	
	@Test
	void wonFight() {
		User user = new User() {{
			setGameAcc(new GameAcc() {{
				setAttackTickets(3);
				setBattlePoints(30L);
				setGold(10L);
			}});
		}};
		User user2 = new User() {{
			setGameAcc(new GameAcc() {{
				setAttackTickets(3);
				setBattlePoints(30L);
				setGold(10L);
			}});
		}};
		
		Mockito.when(this.fightRepository.saveAndFlush(any()))
			.thenReturn(new Fight());
		
		Mockito.when(this.gameAccRepository.saveAndFlush(user.getGameAcc()))
			.thenReturn(user.getGameAcc());
		
		Mockito.when(this.gameAccRepository.saveAndFlush(user2.getGameAcc()))
			.thenReturn(user2.getGameAcc());
		
		Mockito.when(this.userRepository.findByUsername("USER"))
			.thenReturn(Optional.of(user));
		
		Mockito.when(this.userRepository.findByUsername("USER2"))
			.thenReturn(Optional.of(user2));
		
		this.service.wonFight("USER2", "USER", "10", "4");
		
		assertEquals(user.getGameAcc().getAttackTickets(), 2);
		assertEquals(user.getGameAcc().getBattlePoints(), 40);
		assertEquals(user.getGameAcc().getGold(), 15);
		
		assertEquals(user2.getGameAcc().getAttackTickets(), 3);
		assertEquals(user2.getGameAcc().getBattlePoints(), 26);
	}

	@Test
	void lostFight_whenUserDoesNotExist_shouldThrowUserNotFoundException() {		
		Mockito.when(this.userRepository.findByUsername("USER"))
			.thenReturn(Optional.of(new User()));
		
		assertThrows(UserNotFoundException.class, () ->
			this.service.lostFight("USER", "WRONG_USER", "1", "1")
		);
	}
	
	@Test
	void lostFight() {
		User user = new User() {{
			setGameAcc(new GameAcc() {{
				setAttackTickets(3);
				setBattlePoints(30L);
				setGold(10L);
			}});
		}};
		User user2 = new User() {{
			setGameAcc(new GameAcc() {{
				setAttackTickets(3);
				setBattlePoints(30L);
				setGold(10L);
			}});
		}};
		
		Mockito.when(this.fightRepository.saveAndFlush(any()))
			.thenReturn(new Fight());
		
		Mockito.when(this.gameAccRepository.saveAndFlush(user.getGameAcc()))
			.thenReturn(user.getGameAcc());
		
		Mockito.when(this.gameAccRepository.saveAndFlush(user2.getGameAcc()))
			.thenReturn(user2.getGameAcc());
		
		Mockito.when(this.userRepository.findByUsername("USER"))
			.thenReturn(Optional.of(user));
		
		Mockito.when(this.userRepository.findByUsername("USER2"))
			.thenReturn(Optional.of(user2));
		
		this.service.lostFight("USER2", "USER", "4", "10");
		
		assertEquals(user.getGameAcc().getAttackTickets(), 2);
		assertEquals(user.getGameAcc().getBattlePoints(), 26);
		
		assertEquals(user2.getGameAcc().getAttackTickets(), 3);
		assertEquals(user2.getGameAcc().getBattlePoints(), 40);
	}

	@Test
	void resetAttackTickets() {
		List<GameAcc> gameAccs = new ArrayList<>();
		gameAccs.add(user.getGameAcc());
		
		Mockito.when(this.gameAccRepository.findAll())
			.thenReturn(gameAccs);
		
		this.service.resetAttackTickets();
		
		assertEquals(user.getGameAcc().getAttackTickets(), 3);
	}
}
