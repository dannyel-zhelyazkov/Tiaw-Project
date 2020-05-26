package dny.apps.tiaw.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import dny.apps.tiaw.domain.entities.Card;
import dny.apps.tiaw.domain.entities.Deck;
import dny.apps.tiaw.domain.entities.GameAcc;
import dny.apps.tiaw.domain.entities.User;
import dny.apps.tiaw.domain.models.service.DeckCreateServiceModel;
import dny.apps.tiaw.domain.models.service.DeckServiceModel;
import dny.apps.tiaw.error.card.CardNotFoundException;
import dny.apps.tiaw.error.deck.DeckContainsCardException;
import dny.apps.tiaw.error.deck.DeckNotFoundException;
import dny.apps.tiaw.error.deck.DeckSizeException;
import dny.apps.tiaw.error.deck.InvalidDeckCreateException;
import dny.apps.tiaw.error.user.UserNotFoundException;
import dny.apps.tiaw.repository.CardRepository;
import dny.apps.tiaw.repository.DeckRepository;
import dny.apps.tiaw.repository.UserRepository;

class DeckServiceTest extends BaseServiceTest {
	@Autowired
	private DeckService service;
	
	@MockBean
	private DeckRepository deckRepository;
	@MockBean
	private UserRepository userRepository;
	@MockBean
	private CardRepository cardRepository;
	
	@Test
	void findById_whenDeckDoesNotExist_shouldThrowDeckNotFoundException() {
		Mockito.when(this.deckRepository.findById("WRONG_ID"))
			.thenReturn(Optional.empty());
		
		assertThrows(DeckNotFoundException.class, () ->
			this.service.findById("WRONG_ID")
		);
	}
	
	@Test
	void findById_whenDeckExist_shouldReturnDeck() {
		Deck deck = new Deck();
		deck.setId("DECK_ID");
		deck.setName("DECK_NAME");
		
		Mockito.when(this.deckRepository.findById("DECK_ID"))
			.thenReturn(Optional.of(deck));
		
		DeckServiceModel actualDeck = this.service.findById("DECK_ID");
		
		assertEquals(deck.getName(), actualDeck.getName());
	}

	@Test
	void findByOwner_whenOwnerdDoesNotExist_shouldThrowUserNotFoundException() {
		Mockito.when(this.userRepository.findByUsername("WRONG_USER"))
			.thenReturn(Optional.empty());

		Mockito.when(this.deckRepository.findByName("DECK_NAME"))
			.thenReturn(Optional.of(new Deck()));
		
		assertThrows(UserNotFoundException.class, () ->
			this.service.findByOwner("WRONG_USER", "DECK_NAME")
		);
	}
	
	@Test
	void findByOwner_whenDeckDoesNotExist_shouldThrowDeckNotFoundException() {
		User user = new User() {{
			setUsername("USER");
			setGameAcc(new GameAcc() {{
				setDecks(new HashSet<>() {{
					add(new Deck() {{
						setName("DECK_NAME");
					}});
				}});
			}});
		}};
		
		Mockito.when(this.userRepository.findByUsername("USER"))
			.thenReturn(Optional.of(user));

		Mockito.when(this.deckRepository.findByName("WRONG_DECK_NAME"))
			.thenReturn(Optional.empty());
		
		assertThrows(DeckNotFoundException.class, () ->
			this.service.findByOwner("USER", "WRONG_DECK_NAME")
		);
	}
	
	@Test
	void findByOwner_whenDeckAndOwnerExist_shouldReturnDeck() {
		User user = new User() {{
			setUsername("USER");
			setGameAcc(new GameAcc() {{
				setDecks(new HashSet<>() {{
					add(new Deck() {{
						setName("DECK_NAME");
					}});
				}});
			}});
		}};
		
		Mockito.when(this.userRepository.findByUsername("USER"))
			.thenReturn(Optional.of(user));
		
		DeckServiceModel deckServiceModel = this.service.findByOwner("USER", "DECK_NAME");
		
		assertEquals(user.getGameAcc().getDecks().iterator().next().getName(), 
				deckServiceModel.getName());
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
	void findAllByOwner_whenOwnerDoesNotExist_shoudlThrowUserNotFoundException() {
		Mockito.when(this.userRepository.findByUsername("WRONG_NAME"))
			.thenReturn(Optional.empty());
		
		assertThrows(UserNotFoundException.class, () ->
			this.service.findAllDecksByOwner("WRONG_NAME")
		);
	}
	
	@Test
	void findAllByOwner_whenOwnerExist_shoudlReturnAllDecks() {
		User user = new User() {{
			setUsername("USER");
			setGameAcc(new GameAcc() {{
				setDecks(new LinkedHashSet<>() {{
					add(new Deck());
					add(new Deck());
					add(new Deck());
				}});
			}});
		}};
		
		Mockito.when(this.userRepository.findByUsername("USER"))
			.thenReturn(Optional.of(user));
		
		Set<DeckServiceModel> deckServiceModels = this.service.findAllDecksByOwner("USER");
		
		assertEquals(user.getGameAcc().getDecks().size(), deckServiceModels.size());
	}
	
	private DeckCreateServiceModel notValidDeckCreateServiceModel() {
		return new DeckCreateServiceModel() {{
			setName("Na");
		}};
	}
	
	private DeckCreateServiceModel validDeckCreateServiceModel() {
		return new DeckCreateServiceModel() {{
			setName("Deck");
		}};
	}
	
	@Test
	void createDeck_whenDeckIsNotValid_shoudlThrowInvalidDeckCreateException() {
		Mockito.when(this.userRepository.findByUsername("USER"))
		.thenReturn(Optional.of(new User()));
	
		assertThrows(InvalidDeckCreateException.class, () ->
			this.service.createDeck(notValidDeckCreateServiceModel())
		);
	}
	
	@Test
	void createDeck_whenDeckIsValid_shouldCreateDeck() {	
		DeckCreateServiceModel deckCreateServiceModel = validDeckCreateServiceModel();
		
		this.service.createDeck(deckCreateServiceModel);
		
		ArgumentCaptor<Deck> argument = ArgumentCaptor.forClass(Deck.class);
		Mockito.verify(this.deckRepository).saveAndFlush(argument.capture());
		
		Deck deck = argument.getValue();
		
		assertNotNull(deck);
		assertNotNull(deck.getCards());
		assertEquals(deck.getName(), deckCreateServiceModel.getName());
	}

	@Test
	void deleteDeck_whenDeckDoesNotExist_shouldThrowDeckNotFoundException() {
		Mockito.when(this.deckRepository.findById("WRONG_ID"))
			.thenReturn(Optional.empty());
		
		assertThrows(DeckNotFoundException.class, () ->
			this.service.deleteDeck("WRONG_ID")
		);
	}
	
	@Test
	void deleteDeck_whenDeckExist_shouldDeleteDeck() {
		Deck deck = new Deck() {{
			setId("DECK_ID2");
			setName("DECK_NAME2");
		}};
		
		Mockito.when(this.deckRepository.findById("DECK_ID2"))
			.thenReturn(Optional.of(deck));
		
		this.service.deleteDeck("DECK_ID2");
		
		Mockito.verify(this.deckRepository).delete(deck);
	}
	
	@Test
	void addCard_whenUserDoesNotExist_shouldThrowUserNotFoundException() {
		Mockito.when(this.userRepository.findByUsername("WRONG_USER"))
			.thenReturn(Optional.empty());
		
		assertThrows(UserNotFoundException.class, () ->
			this.service.addCard("DECK_NAME", "CARD_ID", "WRONG_USER")
		);
	}
	
	@Test
	void addCard_whenDeckDoesNotExist_shouldThrowDeckNotFoundException() {
		Mockito.when(this.userRepository.findByUsername("USER"))
			.thenReturn(Optional.of(new User() {{
				setGameAcc(new GameAcc() {{
					setDecks(new LinkedHashSet<>());
				}});
			}}));
		
		Mockito.when(this.deckRepository.findByName("DECK_NAME"))
			.thenReturn(Optional.empty());
		
		assertThrows(DeckNotFoundException.class, () ->
			this.service.addCard("DECK_NAME", "CARD_ID", "USER")
		);
	}
	
	@Test
	void addCard_whenDeckIsFull_shouldThrowDeckSizeException() {
		Mockito.when(this.userRepository.findByUsername("USER"))
		.thenReturn(Optional.of(new User() {{
			setGameAcc(new GameAcc() {{
				setDecks(new LinkedHashSet<>());
			}});
		}}));
	
		Mockito.when(this.deckRepository.findByName("DECK_NAME"))
			.thenReturn(Optional.of(new Deck() {{
				setCards(new LinkedHashSet<>() {{
					add(new Card());
					add(new Card());
					add(new Card());
					add(new Card());
					add(new Card());
				}});
			}}));
		
		assertThrows(DeckSizeException.class, () ->
			this.service.addCard("DECK_NAME", "CARD_ID", "USER")
		);
	}
	
	@Test
	void addCard_whenCardDoesNotExist_shouldThrowCardNotFoundException() {
		Mockito.when(this.userRepository.findByUsername("USER"))
		.thenReturn(Optional.of(new User() {{
			setGameAcc(new GameAcc() {{
				setDecks(new LinkedHashSet<>());
			}});
		}}));
	
		Mockito.when(this.deckRepository.findByName("DECK_NAME"))
			.thenReturn(Optional.of(new Deck() {{
				setCards(new LinkedHashSet<>() {{
					add(new Card());
					add(new Card());
					add(new Card());
					add(new Card());
					add(new Card());
				}});
			}}));
		
		Mockito.when(this.cardRepository.findById("WRONG_CARD_ID"))
			.thenReturn(Optional.empty());
		
		assertThrows(DeckSizeException.class, () ->
			this.service.addCard("DECK_NAME", "WRONG_CARD_ID", "USER")
		);
	}
	
	@Test
	void addCard_whenDeckHasTheCard_shouldThrowDeckContainsCardException() {
		Mockito.when(this.userRepository.findByUsername("USER"))
		.thenReturn(Optional.of(new User() {{
			setGameAcc(new GameAcc() {{
				setDecks(new LinkedHashSet<>());
			}});
		}}));
	
		Mockito.when(this.deckRepository.findByName("DECK_NAME"))
			.thenReturn(Optional.of(new Deck() {{
				setCards(new LinkedHashSet<>() {{
					add(new Card() {{
						setId("ID1");
					}});
					add(new Card(){{
						setId("ID2");
					}});
					add(new Card(){{
						setId("ID3");
					}});
					add(new Card(){{
						setId("ID4");
					}});
					add(new Card(){{
						setId("ID5");
					}});
				}});
			}}));
		
		Mockito.when(this.cardRepository.findById("CARD_ID"))
			.thenReturn(Optional.of(new Card() {{
				setId("ID2");
			}}));
		
		assertThrows(DeckContainsCardException.class, () ->
			this.service.addCard("DECK_NAME", "CARD_ID", "USER")
		);
	}

	@Test
	void addCard() {
		User user = new User() {{
			setUsername("USER");
			setGameAcc(new GameAcc() {{
				setDecks(new LinkedHashSet<>() {{
					add(new Deck() {{
						setName("DECK_NAME");
						setCards(new HashSet<>() {{
							add(new Card());
							add(new Card());
							add(new Card());
						}});
					}});
				}});
			}});
		}};
		
		Card card = new Card() {{
			setId("ADD_CARD_ID");
			setName("ADD_CARD");
		}}; 
		
		Mockito.when(this.userRepository.findByUsername("USER"))
			.thenReturn(Optional.of(user));
		
		Mockito.when(this.cardRepository.findById("ADD_CARD_ID"))
			.thenReturn(Optional.of(card));
		
		Mockito.when(this.deckRepository.saveAndFlush(any()))
			.thenReturn(user.getGameAcc().getDecks().stream().findFirst().get());
		
		DeckServiceModel actual = this.service.addCard("DECK_NAME", "ADD_CARD_ID", "USER");
		
		assertEquals(user.getGameAcc().getDecks().iterator().next().getCards().size(), 4);
		assertEquals(user.getGameAcc().getDecks().iterator().next().getCards().size(), actual.getCards().size());
	}

	@Test
	void removeCard_whenDeckDoesNotExist_shouldThrowDeckNotFoudnException() {
		Mockito.when(this.deckRepository.findById("WORONG_DECK_ID"))
			.thenReturn(Optional.empty());
		
		assertThrows(DeckNotFoundException.class, () ->
			this.service.removeCard("WORONG_DECK_ID", "CARD_NAME")
		);
	}
	
	@Test
	void removeCard_whenCardDoesNotExist_shouldThrowCardNotFoudnException() {
		Mockito.when(this.deckRepository.findById("DECK_ID"))
			.thenReturn(Optional.of(new Deck()));
		
		Mockito.when(this.cardRepository.findByName("WRONG_CARD_NAME"))
			.thenReturn(Optional.of(new Card()));
		
		assertThrows(CardNotFoundException.class, () ->
			this.service.removeCard("DECK_ID", "WRONG_CARD_NAME")
		);
	}
	
	@Test
	void testRemoveCard() {
		Deck deck = new Deck() {{
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
			}});
		}};
				
		Mockito.when(this.deckRepository.findById("DECK_ID"))
			.thenReturn(Optional.of(deck));
		
		Mockito.when(this.deckRepository.saveAndFlush(any()))
			.thenReturn(deck);
		
		DeckServiceModel deckServiceModel = this.service.removeCard("DECK_ID", "CARD_NAME2");
		
		assertEquals(deck.getCards().size(), 3);
		assertEquals(deck.getCards().size(), deckServiceModel.getCards().size());
	}
}
