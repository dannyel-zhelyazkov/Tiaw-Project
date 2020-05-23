package dny.apps.tiaw.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import dny.apps.tiaw.domain.entities.Card;
import dny.apps.tiaw.domain.entities.Deck;
import dny.apps.tiaw.domain.entities.GameAcc;
import dny.apps.tiaw.domain.entities.User;
import dny.apps.tiaw.domain.models.service.DeckServiceModel;
import dny.apps.tiaw.error.deck.DeckNotFoundException;
import dny.apps.tiaw.error.user.UserNotFoundException;
import dny.apps.tiaw.repository.CardRepository;
import dny.apps.tiaw.repository.DeckRepository;
import dny.apps.tiaw.repository.UserRepository;

@SpringBootTest
@RunWith(SpringRunner.class)
class DeckServiceImplTest {
	@Autowired
	private DeckService service;
	
	@MockBean
	private DeckRepository deckRepository;
	@MockBean
	private UserRepository userRepository;
	@MockBean
	private CardRepository cardRepository;
	
	@Test
	void testFindById() {
		Optional<Deck> deck = Optional.of(new Deck() {{
			setId("DECK_ID");
			setName("DECK_NAME");
			setCards(new HashSet<Card>() {{
				add(new Card() {{
					setName("CARD_NAME");
				}});
			}});
		}});
		
		Mockito.when(this.deckRepository.findById("DECK_ID"))
			.thenReturn(deck);
		
		DeckServiceModel actualDeck = this.service.findById("DECK_ID");
		
		assertEquals(deck.get().getName(), actualDeck.getName());
		assertEquals(deck.get().getCards().size(), actualDeck.getCards().size());
		assertEquals(deck.get().getCards().iterator().next().getName(),
				actualDeck.getCards().iterator().next().getName());
		
		Exception nonExistentId = assertThrows(DeckNotFoundException.class, () ->
			this.service.findById("WRONG_ID")
		);
		
		assertEquals(nonExistentId.getMessage(), "Deck with given id does not exist!");
	}

	@Test
	void testFindByOwner() {
		Optional<User> user = Optional.of(new User() {{
			setUsername("USER");
			setGameAcc(new GameAcc() {{
				setDecks(new HashSet<>() {{
					add(new Deck() {{
						setName("DECK_NAME");
						}});
					}});
				}});
			}});
		
		Mockito.when(this.userRepository.findByUsername("USER"))
			.thenReturn(user);
		
		DeckServiceModel deckServiceModel = this.service.findByOwner("USER", "DECK_NAME");
		
		assertEquals(user.get().getGameAcc().getDecks().iterator().next().getName(), 
				deckServiceModel.getName());
		
		Exception nonExistentUsername = assertThrows(UserNotFoundException.class, () ->
			this.service.findByOwner("WRONG_USER", "DECK_NAME")
		);
		
		Exception nonExistentName = assertThrows(DeckNotFoundException.class, () ->
			this.service.findByOwner("USER", "WRONG_DECK_NAME")
		);
	
		assertEquals(nonExistentUsername.getMessage(), "User with given username does not exist!");
		assertEquals(nonExistentName.getMessage(), "Deck with given name does not exist!");
	}

	@Test
	void testFindDefenseDeckByOwner() {
		Optional<User> user = Optional.of(new User() {{
			setUsername("USER");
			setGameAcc(new GameAcc() {{
				setDefenseDeck(new Deck() {{
					setName("DEFENSE_DECK");
				}});
				setDecks(new HashSet<>() {{
					add(new Deck() {{
						setName("DECK_NAME");
					}});
				}});
			}});
		}});
		
		Mockito.when(this.userRepository.findByUsername("USER"))
			.thenReturn(user);
		
		DeckServiceModel deckServiceModel = this.service.findDefenseDeckByOwner("USER");
		
		assertEquals(user.get().getGameAcc().getDefenseDeck().getName(), 
				deckServiceModel.getName());
		
		Exception nonExistentUsername = assertThrows(UserNotFoundException.class, () ->
			this.service.findDefenseDeckByOwner("WRONG_USER")
		);
		
		assertEquals(nonExistentUsername.getMessage(), "User with given username does not exist!");
	}

	@Test
	void testFindAllDecksByOwner() {
		Optional<User> user = Optional.of(new User() {{
			setUsername("USER");
			setGameAcc(new GameAcc() {{
				setDecks(new LinkedHashSet<>() {{
					add(new Deck() {{
						setName("DECK_NAME");
					}});
					add(new Deck() {{
						setName("DECK_NAME2");
					}});
					add(new Deck() {{
						setName("DECK_NAME3");
					}});
				}});
			}});
		}});
		
		Mockito.when(this.userRepository.findByUsername("USER"))
			.thenReturn(user);
		
		Set<DeckServiceModel> deckServiceModels = this.service.findAllDecksByOwner("USER");
		
		assertEquals(user.get().getGameAcc().getDecks().size(), deckServiceModels.size());
		assertEquals(user.get().getGameAcc().getDecks().iterator().next().getName(),
				deckServiceModels.iterator().next().getName());
		
		Exception nonExistentUsername = assertThrows(UserNotFoundException.class, () ->
			this.service.findAllDecksByOwner("WRONG_USER")
		);
	
		assertEquals(nonExistentUsername.getMessage(), "User with given username does not exist!");
	}

	@Test
	void testCreateDeck() {		
		DeckServiceModel deckServiceModel = new DeckServiceModel() {{
			setName("Deck");
		}};
						
		DeckServiceModel actual = this.service.createDeck(deckServiceModel);
		
		Mockito.verify(this.deckRepository, Mockito.times(1))
			.saveAndFlush(any(Deck.class));
		
		assertNotNull(deckServiceModel.getCards());
		assertNotNull(actual.getCards());
		assertEquals(deckServiceModel.getName(), actual.getName());
	}

	@Test
	void testDeleteDeck() {
		Optional<Deck> deck = Optional.of(new Deck() {{
			setId("DECK_ID");
			setName("DECK_NAME");
			setCards(new HashSet<Card>() {{
				add(new Card() {{
					setName("CARD_NAME");
				}});
			}});
		}});
		
		Mockito.when(this.deckRepository.findById("DECK_ID"))
			.thenReturn(deck);
		
		DeckServiceModel deckServiceModel = this.service.deleteDeck("DECK_ID");
		
		assertEquals(deck.get().getName(), deckServiceModel.getName());
		
		Mockito.verify(this.deckRepository, Mockito.times(1))
			.delete(deck.get());
		
		Exception exception = assertThrows(DeckNotFoundException.class, () ->
			this.service.deleteDeck("WRONG_ID")
		);
		
		assertEquals(exception.getMessage(), "Deck with given id does not exist!");
	}

	@Test
	void testAddCard() {
		Optional<User> user = Optional.of(new User() {{
			setUsername("USER");
			setGameAcc(new GameAcc() {{
				setDecks(new LinkedHashSet<>() {{
					add(new Deck() {{
						setName("DECK_NAME");
						setCards(new HashSet<>() {{
							add(new Card() {{
								setId("CARD_ID");
								setName("CARD_NAME");
							}});
							add(new Card() {{
								setId("CARD_ID2");
								setName("CARD_NAME2");
							}});
							add(new Card() {{
								setId("CARD_ID3");
								setName("CARD_NAME3");
							}});
							add(new Card() {{
								setId("CARD_ID4");
								setName("CARD_NAME4");
							}});
						}});
					}});
				}});
			}});
		}});
		
		Optional<Card> card = Optional.of(new Card() {{
			setId("ADD_CARD_ID");
			setName("ADD_CARD");
		}}); 
		
		Mockito.when(this.userRepository.findByUsername("USER"))
			.thenReturn(user);
		
		Mockito.when(this.cardRepository.findById("ADD_CARD_ID"))
			.thenReturn(card);
		
		Mockito.when(this.deckRepository.saveAndFlush(any()))
			.thenReturn(user.get().getGameAcc().getDecks().stream().findFirst().get());
		
		DeckServiceModel actual = this.service.addCard("DECK_NAME", "ADD_CARD_ID", "USER");
		
		assertEquals(user.get().getGameAcc().getDecks().iterator().next().getCards().size(), 5);
		assertEquals(user.get().getGameAcc().getDecks().iterator().next().getCards().size(), 
				actual.getCards().size());
		
//		Exception exceptionUsername = assertThrows(UserNotFoundException.class, () ->
//			this.service.addCard("DECK_NAME", "ADD_CARD_ID", "WRONG_USER")
//		);
//		
//		Exception exceptionDeckName = assertThrows(UserNotFoundException.class, () ->
//			this.service.addCard("WRONG_DECK_NAME", "ADD_CARD_ID", "USER")
//		);
//		
////		Exception exceptionDeckSize = assertThrows(DeckSizeException.class, () ->
////			this.service.addCard("DECK_NAME", "CARD_ID", "USER")
////		);
//		
//		Exception exceptionCardId = assertThrows(UserNotFoundException.class, () ->
//			this.service.addCard("DECK_NAME", "WRONG_CARD_ID", "USER")
//		);
//		
////		Exception exceptionContaisCard = assertThrows(DeckContainsCardException.class, () ->
////			this.service.addCard("DECK_NAME", "CARD_ID", "USER")
////		);
//		assertEquals(exceptionUsername.getMessage(), "User with given username does not exist!");
//		assertEquals(exceptionDeckName.getMessage(), "Deck with given doest not exist!");
//		assertEquals(exceptionCardId.getMessage(), "Card with given id was not found!");
	}

	@Test
	void testRemoveCard() {
		Optional<Deck> deck = Optional.of(new Deck() {{
			setId("DECK_ID");
			setName("DECK");
			setCards(new LinkedHashSet<>() {{
				add(new Card() {{
					setId("CARD_ID");
					setName("CARD_NAME");
				}});
				add(new Card() {{
					setId("CARD_ID2");
					setName("CARD_NAME2");
				}});
				add(new Card() {{
					setId("CARD_ID3");
					setName("CARD_NAME3");
				}});
				add(new Card() {{
					setId("CARD_ID4");
					setName("CARD_NAME4");
				}});
			}});
		}});
				
		Mockito.when(this.deckRepository.findById("DECK_ID"))
			.thenReturn(deck);
		
		Mockito.when(this.deckRepository.saveAndFlush(any()))
			.thenReturn(deck.get());
		
		DeckServiceModel deckServiceModel = this.service.removeCard("DECK_ID", "CARD_NAME2");
		
		assertEquals(deck.get().getCards().size(), 3);
		assertEquals(deck.get().getCards().size(), deckServiceModel.getCards().size());
	}
}
