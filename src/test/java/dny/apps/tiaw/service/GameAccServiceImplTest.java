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

@SpringBootTest
@RunWith(SpringRunner.class)
class GameAccServiceImplTest {

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
	void testFindByUser() {
		Optional<User> user = Optional.of(new User() {{
			setUsername("USER");
			setGameAcc(new GameAcc() {{
				setBattlePoints(3040L);
			}});
		}});
		
		Mockito.when(this.userRepository.findByUsername("USER"))
			.thenReturn(user);
		
		GameAccServiceModel gameAccServiceModel = this.service.findByUser("USER");
		
		assertEquals(user.get().getUsername(), gameAccServiceModel.getUsername());
		assertEquals(gameAccServiceModel.getBattlePoints(), 3040L);
		assertEquals(gameAccServiceModel.getUsername(), "USER");
		
		Exception exception = assertThrows(UserNotFoundException.class, () ->
			this.service.findByUser("WRONG_USER")
		);
		
		assertEquals(exception.getMessage(), "User with given username does not exist!");
	}

	@Test
	void testFindAll() {
		Optional<User> user1 = Optional.of(new User() {{
			setUsername("USER");
			setGameAcc(new GameAcc() {{
				setBattlePoints(3040L);
			}});
		}});
		Optional<User> user2 = Optional.of(new User() {{
			setUsername("USER2");
			setGameAcc(new GameAcc() {{
				setBattlePoints(3060L);
			}});
		}});
		Optional<User> user3 = Optional.of(new User() {{
			setUsername("USER3");
			setGameAcc(new GameAcc() {{
				setBattlePoints(3080L);
			}});
		}});
		
		List<User> users = new ArrayList<>();
		users.add(user1.get());
		users.add(user2.get());
		users.add(user3.get());
		
		Mockito.when(this.userRepository.findAll())
			.thenReturn(users);
		
		List<GameAccServiceModel> gameAccServiceModels = this.service.findAll();
		
		assertEquals(users.size(), gameAccServiceModels.size());
		assertEquals(users.get(0).getGameAcc().getBattlePoints(), gameAccServiceModels.get(0).getBattlePoints());
		assertEquals(users.get(0).getUsername(), gameAccServiceModels.get(0).getUsername());
	}

	@Test
	void testFindAllFightGameAccs() {
		Optional<User> user1 = Optional.of(new User() {{
			setUsername("USER");
			setGameAcc(new GameAcc() {{
				setDefenseDeck(new Deck());
				setBattlePoints(3040L);
			}});
		}});
		Optional<User> user2 = Optional.of(new User() {{
			setUsername("USER2");
			setGameAcc(new GameAcc() {{
				setDefenseDeck(new Deck());
				setBattlePoints(3060L);
			}});
		}});
		Optional<User> user3 = Optional.of(new User() {{
			setUsername("USER3");
			setGameAcc(new GameAcc() {{
				setBattlePoints(3080L);
			}});
		}});
		
		List<User> users = new ArrayList<>();
		users.add(user1.get());
		users.add(user2.get());
		users.add(user3.get());
		
		Mockito.when(this.userRepository.findAll())
			.thenReturn(users);
	
		List<GameAccServiceModel> gameAccServiceModels = this.service.findAllFightGameAccs();
		
		assertNotEquals(users.size(), gameAccServiceModels.size());
		assertEquals(gameAccServiceModels.size(), 2);
		assertEquals(users.get(0).getGameAcc().getBattlePoints(), gameAccServiceModels.get(0).getBattlePoints());
		assertEquals(users.get(0).getUsername(), gameAccServiceModels.get(0).getUsername());
	}

	@Test
	void testBuyCard() {
		Optional<User> user = Optional.of(new User() {{
			setUsername("USER");
			setGameAcc(new GameAcc() {{
				setDefenseDeck(new Deck());
				setGold(300L);
				setCards(new LinkedHashSet<>() {{
					add(new Card() {{
						setName("Card01");
					}});
					add(new Card() {{
						setName("Card02");
					}});
				}});
			}});
		}});
		
		Optional<Card> card = Optional.of(new Card() {{
			setId("CARD_ID");
			setName("Card03");
			setPrice(10);
		}});
		
		Mockito.when(this.gameAccRepository.saveAndFlush(any()))
			.thenReturn(user.get().getGameAcc());
		
		Mockito.when(this.userRepository.findByUsername("USER"))
			.thenReturn(user);
		
		Mockito.when(this.cardRepository.findById("CARD_ID"))
			.thenReturn(card);
		
		this.service.buyCard("CARD_ID", "USER");
		
		assertEquals(user.get().getGameAcc().getCards().size(), 3);
		
		Exception exceptionUser = assertThrows(UserNotFoundException.class, () ->
			this.service.buyCard("CARD_ID", "WRONG_USER")
		);
		
		Exception exceptionCard = assertThrows(CardNotFoundException.class, () ->
			this.service.buyCard("WRONG_CARD_ID", "USER")
		);
		
		assertEquals(exceptionUser.getMessage(), "User with given username does not exist!");
		assertEquals(exceptionCard.getMessage(), "Card with given id does not exist!");
	}

	@Test
	void testAddDeck() {
		Optional<User> user = Optional.of(new User() {{
			setUsername("USER");
			setGameAcc(new GameAcc() {{
				setDefenseDeck(new Deck());
				setGold(300L);
				setDecks(new LinkedHashSet<>() {{
					add(new Deck() {{
						setName("DECK");
					}});
					add(new Deck() {{
						setName("DECK2");
					}});
					add(new Deck() {{
						setName("DECK3");
					}});
				}});
			}});
		}});
		
		Optional<Deck> deck = Optional.of(new Deck() {{
			setId("DECK_ID");
			setName("DECK4");
		}});
		
		Mockito.when(this.gameAccRepository.saveAndFlush(any()))
			.thenReturn(user.get().getGameAcc());
	
		Mockito.when(this.userRepository.findByUsername("USER"))
			.thenReturn(user);
		
		Mockito.when(this.deckRepository.findById("DECK_ID"))
			.thenReturn(deck);
		
		this.service.addDeck("DECK_ID", "USER");
		
		assertEquals(user.get().getGameAcc().getDecks().size(), 4);
		
		Exception exceptionUser = assertThrows(UserNotFoundException.class, () ->
			this.service.addDeck("CARD_ID", "WRONG_USER")
		);
		
		Exception exceptionDeck = assertThrows(DeckNotFoundException.class, () ->
			this.service.addDeck("WRONG_DECK_ID", "USER")
		);
		
		assertEquals(exceptionUser.getMessage(), "User with given username does not exist!");
		assertEquals(exceptionDeck.getMessage(), "Deck with given id does not exist!");
	}

	@Test
	void testSetDefense() {
		Optional<User> user = Optional.of(new User() {{
			setUsername("USER");
			setGameAcc(new GameAcc());
		}});
		
		Optional<Deck> deck = Optional.of(new Deck() {{
			setId("DECK_ID");
			setName("DECK4");
		}});
		
		Mockito.when(this.gameAccRepository.saveAndFlush(any()))
			.thenReturn(user.get().getGameAcc());
		
		Mockito.when(this.deckRepository.saveAndFlush(any()))
			.thenReturn(deck.get());

		Mockito.when(this.userRepository.findByUsername("USER"))
			.thenReturn(user);
		
		Mockito.when(this.deckRepository.findById("DECK_ID"))
			.thenReturn(deck);
		
		this.service.setDefense("DECK_ID", "USER");
		
		assertEquals(user.get().getGameAcc().getDefenseDeck().getName(), "DECK4");
		
		Exception exceptionUser = assertThrows(UserNotFoundException.class, () ->
			this.service.setDefense("CARD_ID", "WRONG_USER")
		);
		
		Exception exceptionDeck = assertThrows(DeckNotFoundException.class, () ->
			this.service.setDefense("WRONG_DECK_ID", "USER")
		);
		
		assertEquals(exceptionUser.getMessage(), "User with given username does not exist!");
		assertEquals(exceptionDeck.getMessage(), "Deck with given id does not exist!");
	}

	@Test
	void testSetAttack() {
		Optional<User> user = Optional.of(new User() {{
			setUsername("USER");
			setGameAcc(new GameAcc());
		}});
		
		Optional<Deck> deck = Optional.of(new Deck() {{
			setId("DECK_ID");
			setName("DECK4");
		}});
		
		Mockito.when(this.gameAccRepository.saveAndFlush(any()))
			.thenReturn(user.get().getGameAcc());
		
		Mockito.when(this.deckRepository.saveAndFlush(any()))
			.thenReturn(deck.get());

		Mockito.when(this.userRepository.findByUsername("USER"))
			.thenReturn(user);
		
		Mockito.when(this.deckRepository.findById("DECK_ID"))
			.thenReturn(deck);
		
		this.service.setAttack("DECK_ID", "USER");
		
		assertEquals(user.get().getGameAcc().getAttackDeck().getName(), "DECK4");
		
		Exception exceptionUser = assertThrows(UserNotFoundException.class, () ->
			this.service.setAttack("CARD_ID", "WRONG_USER")
		);
		
		Exception exceptionDeck = assertThrows(DeckNotFoundException.class, () ->
			this.service.setAttack("WRONG_DECK_ID", "USER")
		);
		
		assertEquals(exceptionUser.getMessage(), "User with given username does not exist!");
		assertEquals(exceptionDeck.getMessage(), "Deck with given id does not exist!");
	}

	@Test
	void testRemoveDeck() {
		Optional<User> user = Optional.of(new User() {{
			setUsername("USER");
			setGameAcc(new GameAcc() {{
				setDecks(new LinkedHashSet<>() {{
					add(new Deck() {{
						setId("DECK_ID");
						setName("DECK");
					}});
					add(new Deck() {{
						setId("DECK_ID2");
						setName("DECK2");
					}});
					add(new Deck() {{
						setId("DECK_ID3");
						setName("DECK3");
					}});
				}});
			}});
		}});
		
		Optional<Deck> deck = Optional.of(new Deck() {{
			setId("DECK_ID3");
			setName("DECK3");
		}});
		
		Mockito.when(this.gameAccRepository.saveAndFlush(any()))
			.thenReturn(user.get().getGameAcc());
	
		Mockito.when(this.userRepository.findByUsername("USER"))
			.thenReturn(user);
		
		Mockito.when(this.deckRepository.findById("DECK_ID3"))
			.thenReturn(deck);
		
		this.service.removeDeck("DECK_ID3", "USER");
		
		assertEquals(user.get().getGameAcc().getDecks().size(), 2);
		
		Exception exceptionUser = assertThrows(UserNotFoundException.class, () ->
			this.service.setAttack("CARD_ID3", "WRONG_USER")
		);
	
		Exception exceptionDeck = assertThrows(DeckNotFoundException.class, () ->
			this.service.setAttack("WRONG_DECK_ID", "USER")
		);
	
		assertEquals(exceptionUser.getMessage(), "User with given username does not exist!");
		assertEquals(exceptionDeck.getMessage(), "Deck with given id does not exist!");
	}

	@Test
	void testWonFight() {
		Optional<User> user = Optional.of(new User() {{
			setUsername("USER");
			setGameAcc(new GameAcc() {{
				setAttackTickets(3);
				setBattlePoints(30L);
				setGold(10L);
			}});
		}});
		Optional<User> user2 = Optional.of(new User() {{
			setUsername("USER2");
			setGameAcc(new GameAcc() {{
				setAttackTickets(3);
				setBattlePoints(30L);
				setGold(10L);
			}});
		}});
		
		Mockito.when(this.fightRepository.saveAndFlush(any()))
			.thenReturn(new Fight());
		
		Mockito.when(this.gameAccRepository.saveAndFlush(user.get().getGameAcc()))
			.thenReturn(user.get().getGameAcc());
		
		Mockito.when(this.gameAccRepository.saveAndFlush(user2.get().getGameAcc()))
			.thenReturn(user2.get().getGameAcc());
		
		Mockito.when(this.userRepository.findByUsername("USER"))
			.thenReturn(user);
		
		Mockito.when(this.userRepository.findByUsername("USER2"))
			.thenReturn(user2);
		
		this.service.wonFight("USER2", "USER", "10", "4");
		
		assertEquals(user.get().getGameAcc().getAttackTickets(), 2);
		assertEquals(user.get().getGameAcc().getBattlePoints(), 40);
		assertEquals(user.get().getGameAcc().getGold(), 15);
		
		assertEquals(user2.get().getGameAcc().getAttackTickets(), 3);
		assertEquals(user2.get().getGameAcc().getBattlePoints(), 26);
		
		Exception exceptionUser = assertThrows(UserNotFoundException.class, () ->
			this.service.wonFight("WRONG_USER2", "USER", "10", "4")
		);
		
		assertEquals(exceptionUser.getMessage(), "User with given username does not exist!");
	}

	@Test
	void testLostFight() {
		Optional<User> user = Optional.of(new User() {{
			setUsername("USER");
			setGameAcc(new GameAcc() {{
				setAttackTickets(3);
				setBattlePoints(30L);
				setGold(10L);
			}});
		}});
		Optional<User> user2 = Optional.of(new User() {{
			setUsername("USER2");
			setGameAcc(new GameAcc() {{
				setAttackTickets(3);
				setBattlePoints(30L);
				setGold(10L);
			}});
		}});
		
		Mockito.when(this.fightRepository.saveAndFlush(any()))
			.thenReturn(new Fight());
		
		Mockito.when(this.gameAccRepository.saveAndFlush(user.get().getGameAcc()))
			.thenReturn(user.get().getGameAcc());
		
		Mockito.when(this.gameAccRepository.saveAndFlush(user2.get().getGameAcc()))
			.thenReturn(user2.get().getGameAcc());
		
		Mockito.when(this.userRepository.findByUsername("USER"))
			.thenReturn(user);
		
		Mockito.when(this.userRepository.findByUsername("USER2"))
			.thenReturn(user2);
		
		this.service.lostFight("USER2", "USER", "10", "4");
		
		assertEquals(user.get().getGameAcc().getAttackTickets(), 2);
		assertEquals(user.get().getGameAcc().getBattlePoints(), 20);
		
		assertEquals(user2.get().getGameAcc().getAttackTickets(), 3);
		assertEquals(user2.get().getGameAcc().getBattlePoints(), 34);
		
		Exception exceptionUser = assertThrows(UserNotFoundException.class, () ->
			this.service.wonFight("WRONG_USER2", "USER", "10", "4")
		);
		
		assertEquals(exceptionUser.getMessage(), "User with given username does not exist!");
	}

	@Test
	void testResetAttackTickets() {
		Optional<User> user = Optional.of(new User() {{
			setUsername("USER");
			setGameAcc(new GameAcc() {{
				setAttackTickets(10);
				setBattlePoints(30L);
				setGold(10L);
			}});
		}});
		
		List<GameAcc> gameAccs = new ArrayList<>();
		gameAccs.add(user.get().getGameAcc());
		
		Mockito.when(this.gameAccRepository.findAll())
			.thenReturn(gameAccs);
		
		this.service.resetAttackTickets();
		
		assertEquals(user.get().getGameAcc().getAttackTickets(), 3);
	}

}
