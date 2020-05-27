package dny.apps.tiaw.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

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
	
	@Test
	void findByUser_whenUserDoesNotExist_shouldThrowUserNotFoudnException() {
		Mockito.when(this.userRepository.findByUsername("WRONG_USER"))
			.thenReturn(Optional.empty());
		
		assertThrows(UserNotFoundException.class, () -> 
			this.service.findByUser("WRONG_USER")
		);
	}
	
	@Test
	void findByUser_whenUserExists_shouldReturnGameAcc() {
		User user = new User();
		user.setUsername("USER");
		user.setGameAcc(new GameAcc());
		
		Mockito.when(this.userRepository.findByUsername("USER"))
			.thenReturn(Optional.of(user));
		
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
				setBattlePoints(3040L);
			}});
		}};
		User user2 = new User() {{
			setGameAcc(new GameAcc() {{
				setDefenseDeck(new Deck());
				setBattlePoints(3040L);
			}});
		}};
		
		List<User> users = new ArrayList<>();
		users.add(user1);
		users.add(user2);
		
		Mockito.when(this.userRepository.findAll())
			.thenReturn(users);
	
		List<GameAccServiceModel> gameAccServiceModels = this.service.findAllFightGameAccs();
		
		assertEquals(gameAccServiceModels.size(), 1);
	}

	@Test
	void buyCard_whenUserDoesNotExist_shouldThrowUserNotFoundException() {
		Mockito.when(this.userRepository.findByUsername("WRONG_USER"))
			.thenReturn(Optional.empty());
		
		assertThrows(UserNotFoundException.class, () ->
			this.service.buyCard("CARD_ID", "WRONG_USER")
		);
	}
	@Test
	void buyCard_whenCardDoesNotExist_shouldThrowCardNotFoundException() {
		Mockito.when(this.userRepository.findByUsername("USER"))
			.thenReturn(Optional.of(new User() {{
				setGameAcc(new GameAcc());
			}}));
		
		Mockito.when(this.cardRepository.findById("WRONG_CARD_ID"))
			.thenReturn(Optional.empty());
		
		assertThrows(CardNotFoundException.class, () ->
			this.service.buyCard("WRONG_CARD_ID", "USER")
		);
	}
	
	@Test
	void buyCard() {
		User user = new User() {{
			setGameAcc(new GameAcc() {{
				setGold(300L);
				setCards(new LinkedHashSet<>() {{
					add(new Card() {{
						setName("Card01");
					}});
				}});
			}});
		}};
		
		Mockito.when(this.gameAccRepository.saveAndFlush(any()))
			.thenReturn(user.getGameAcc());
		
		Mockito.when(this.userRepository.findByUsername("USER"))
			.thenReturn(Optional.of(user));
		
		Mockito.when(this.cardRepository.findById("CARD_ID"))
			.thenReturn(Optional.of(new Card() {{
				setPrice(10);
			}}));
		
		this.service.buyCard("CARD_ID", "USER");
		
		assertEquals(user.getGameAcc().getCards().size(), 2);
	}
	
	@Test
	void setDefenseDeck_whenUserDoesNotExist_shouldThrowUserNotFoundException() {
		Mockito.when(this.userRepository.findByUsername("WRONG_USER"))
			.thenReturn(Optional.empty());
		
		assertThrows(UserNotFoundException.class, () ->
			this.service.setDefenseDeck("DECK_ID", "WRONG_USER")
		);
	}
	
	@Test
	void setDefenseDeck_whenUserDeckDoesNotExist_shouldThrowDeckNotFoundException() {
		Mockito.when(this.userRepository.findByUsername("USER"))
			.thenReturn(Optional.of(new User() {{
				setGameAcc(new GameAcc());
			}}));
		
		Mockito.when(this.deckRepository.findById("WRONG_ID"))
		.thenReturn(Optional.empty());
		
		assertThrows(DeckNotFoundException.class, () ->
			this.service.setDefenseDeck("WRONG_ID", "USER")
		);
	}

	@Test
	void setDefenseDeck() {
		User user = new User();
		user.setGameAcc(new GameAcc());
		
		Mockito.when(this.gameAccRepository.saveAndFlush(any()))
			.thenReturn(user.getGameAcc());
		
		Mockito.when(this.deckRepository.saveAndFlush(any()))
			.thenReturn(new Deck());

		Mockito.when(this.userRepository.findByUsername("USER"))
			.thenReturn(Optional.of(user));
		
		Mockito.when(this.deckRepository.findById("DECK_ID"))
			.thenReturn(Optional.of(new Deck()));
		
		this.service.setDefenseDeck("DECK_ID", "USER");
		
		assertNotNull(user.getGameAcc().getDefenseDeck());
	}

	@Test
	void setAttackDeck_whenUserDoesNotExist_shouldThrowUserNotFoundException() {
		Mockito.when(this.userRepository.findByUsername("WRONG_USER"))
			.thenReturn(Optional.empty());
		
		assertThrows(UserNotFoundException.class, () ->
			this.service.setAttackDeck("DECK_ID", "WRONG_USER")
		);
	}
	
	@Test
	void setAttackDeck_whenUserDeckDoesNotExist_shouldThrowDeckNotFoundException() {
		Mockito.when(this.userRepository.findByUsername("USER"))
			.thenReturn(Optional.of(new User() {{
				setGameAcc(new GameAcc());
			}}));
		
		Mockito.when(this.deckRepository.findById("WRONG_ID"))
		.thenReturn(Optional.empty());
		
		assertThrows(DeckNotFoundException.class, () ->
			this.service.setAttackDeck("WRONG_ID", "USER")
		);
	}
	
	@Test
	void setAttackDeck() {
		User user = new User();
		user.setGameAcc(new GameAcc());
		
		Mockito.when(this.gameAccRepository.saveAndFlush(any()))
			.thenReturn(user.getGameAcc());
		
		Mockito.when(this.deckRepository.saveAndFlush(any()))
			.thenReturn(new Deck());

		Mockito.when(this.userRepository.findByUsername("USER"))
			.thenReturn(Optional.of(user));
		
		Mockito.when(this.deckRepository.findById("DECK_ID"))
			.thenReturn(Optional.of(new Deck()));
		
		this.service.setDefenseDeck("DECK_ID", "USER");
		
		assertNotNull(user.getGameAcc().getDefenseDeck());
	}
	
	@Test
	void wonFight_whenUserDoesNotExist_shouldThrowUserNotFoundException() {
		Mockito.when(this.userRepository.findByUsername("WRONG_USER"))
			.thenReturn(Optional.empty());
		
		assertThrows(UserNotFoundException.class, () ->
			this.service.wonFight("WRONG_USER", "USER", "1", "1")
		);
	}
	
	@Test
	void wonFight() {
		User user =new User() {{
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
		Mockito.when(this.userRepository.findByUsername("WRONG_USER"))
			.thenReturn(Optional.empty());
		
		Mockito.when(this.userRepository.findByUsername("USER"))
			.thenReturn(Optional.of(new User()));
		
		assertThrows(UserNotFoundException.class, () ->
			this.service.lostFight("USER", "WRONG_USER", "1", "1")
		);
	}
	
	@Test
	void lostFight() {
		User user =new User() {{
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
		assertEquals(user.getGameAcc().getBattlePoints(), 26);
		
		assertEquals(user2.getGameAcc().getAttackTickets(), 3);
		assertEquals(user2.getGameAcc().getBattlePoints(), 40);
	}

	@Test
	void resetAttackTickets() {
		User user = new User();
		user.setGameAcc(new GameAcc());
		user.getGameAcc().setAttackTickets(0);
		
		List<GameAcc> gameAccs = new ArrayList<>();
		gameAccs.add(user.getGameAcc());
		
		Mockito.when(this.gameAccRepository.findAll())
			.thenReturn(gameAccs);
		
		this.service.resetAttackTickets();
		
		assertEquals(user.getGameAcc().getAttackTickets(), 3);
	}
}
