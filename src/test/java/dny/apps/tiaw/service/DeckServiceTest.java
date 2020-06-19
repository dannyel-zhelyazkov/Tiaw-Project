package dny.apps.tiaw.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
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
import dny.apps.tiaw.repository.GameAccRepository;
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
	@MockBean
	private GameAccRepository gameAccRepository;

	private User user;

	@BeforeEach
	public void setUp() {
		Mockito.when(this.userRepository.findByUsername("WRONG_USER")).thenReturn(Optional.empty());

		this.user = new User();
		this.user.setUsername("USER");
		this.user.setGameAcc(new GameAcc());
		
		Deck defenseDeck = new Deck();
		defenseDeck.setName("DEFENSE_DECK");
		defenseDeck.setId("DEFENSE_DECK_ID");
		this.user.getGameAcc().setDefenseDeck(defenseDeck);
		
		this.user.getGameAcc().setDecks(new ArrayList<>());
		
		Deck deck = new Deck();
		deck.setName("DECK_NAME");
		deck.setId("DECK_ID");
		deck.setCards(new ArrayList<>());
		
		Card card = new Card();
		card.setId("CARD_ID");
		deck.getCards().add(card);
		deck.getCards().add(new Card() {{setId("ID1");}});
		deck.getCards().add(new Card() {{setId("ID2");}});
		deck.getCards().add(new Card() {{setId("ID3");}});
		
		this.user.getGameAcc().getDecks().add(deck);
		this.user.getGameAcc().getDecks().add(new Deck() {{setName("DECK1"); setId("DECK_ID1");}});
		this.user.getGameAcc().getDecks().add(new Deck() {{setName("DECK2"); setId("DECK_ID2");}});
		this.user.getGameAcc().getDecks().add(new Deck() {{setName("DECK3"); setId("DECK_ID3");}});

		Mockito.when(this.userRepository.findByUsername("USER"))
			.thenReturn(Optional.of(this.user));
		Mockito.when(this.deckRepository.findByName("DECK_NMAE"))
			.thenReturn(Optional.of(this.user.getGameAcc().getDecks().get(0)));
	}

	@Test
	void findById_whenDeckDoesNotExist_shouldThrowDeckNotFoundException() {
		assertThrows(DeckNotFoundException.class, () -> this.service.findById("WRONG_ID"));
	}

	// Deck
	@Test
	void findById_whenDeckExist_shouldReturnDeck() {
		Deck deck = new Deck();
		deck.setName("DECK_NAME");

		Mockito.when(this.deckRepository.findById("DECK_ID")).thenReturn(Optional.of(deck));

		DeckServiceModel actualDeck = this.service.findById("DECK_ID");

		assertEquals(deck.getName(), actualDeck.getName());
	}

	@Test
	void findByOwner_whenOwnerdDoesNotExist_shouldThrowUserNotFoundException() {
		Mockito.when(this.deckRepository.findByName("DECK_NAME")).thenReturn(Optional.of(new Deck()));

		assertThrows(UserNotFoundException.class, () -> this.service.findByOwner("WRONG_USER", "DECK_NAME"));
	}

	@Test
	void findByOwner_whenDeckDoesNotExist_shouldThrowDeckNotFoundException() {
		Mockito.when(this.deckRepository.findByName("WRONG_DECK_NAME")).thenReturn(Optional.empty());

		assertThrows(DeckNotFoundException.class, () -> this.service.findByOwner("USER", "WRONG_DECK_NAME"));
	}

	@Test
	void findByOwner_whenDeckAndOwnerExist_shouldReturnDeck() {
		DeckServiceModel deckServiceModel = this.service.findByOwner("USER", "DECK_NAME");

		assertEquals(user.getGameAcc().getDecks().iterator().next().getName(), deckServiceModel.getName());
	}

	@Test
	void findDefenseDeckByOwner_whenOwnerDoesNotExist_shoudlThrowUserNotFoundException() {
		assertThrows(UserNotFoundException.class, () -> 
			this.service.findDefenseDeckByOwner("WRONG_USER")
		);
	}
	
	@Test
	void findDefenseDeckByOwner_whenOwnerExist_shouldReturnDefenseDeck() {
		DeckServiceModel deckServiceModel = this.service.findDefenseDeckByOwner("USER");

		assertEquals(user.getGameAcc().getDefenseDeck().getName(), deckServiceModel.getName());
	}

	@Test
	void findAllByOwner_whenOwnerDoesNotExist_shoudlThrowUserNotFoundException() {
		assertThrows(UserNotFoundException.class, () -> this.service.findAllDecksByOwner("WRONG_USER"));
	}

	@Test
	void findAllByOwner_whenOwnerExist_shoudlReturnAllDecks() {
		List<DeckServiceModel> deckServiceModels = this.service.findAllDecksByOwner("USER");

		assertEquals(user.getGameAcc().getDecks().size(), deckServiceModels.size());
	}

	private DeckCreateServiceModel notValidDeckCreateServiceModel() {
		return new DeckCreateServiceModel() {
			{
				setName("Na");
			}
		};
	}

	private DeckCreateServiceModel validDeckCreateServiceModel() {
		return new DeckCreateServiceModel() {
			{
				setName("Deck");
			}
		};
	}

	@Test
	void createDeck_whenDeckIsNotValid_shoudlThrowInvalidDeckCreateException() {
		assertThrows(InvalidDeckCreateException.class,
				() -> this.service.createDeck(notValidDeckCreateServiceModel(), "USER"));
	}

	@Test
	void createDeck_whenUserDoesNotExist_shoudlThrowUserNotFoundException() {
		assertThrows(UserNotFoundException.class,
				() -> this.service.createDeck(notValidDeckCreateServiceModel(), "WRONG_USER"));
	}

	@Test
	void createDeck_whenDeckIsValid_shouldCreateDeck() {
		DeckCreateServiceModel deckCreateServiceModel = validDeckCreateServiceModel();
		
		Mockito.when(this.gameAccRepository.saveAndFlush(any())).thenReturn(user.getGameAcc());

		Mockito.when(this.deckRepository.findByName(deckCreateServiceModel.getName()))
				.thenReturn(Optional.of(new Deck()));

		this.service.createDeck(deckCreateServiceModel, "USER");

		ArgumentCaptor<Deck> argument = ArgumentCaptor.forClass(Deck.class);
		Mockito.verify(this.deckRepository).saveAndFlush(argument.capture());

		Deck deck = argument.getValue();

		assertNotNull(deck);
		assertNotNull(deck.getCards());
		assertEquals(deck.getName(), deckCreateServiceModel.getName());
	}

	@Test
	void deleteDeck_whenDeckDoesNotExist_shouldThrowDeckNotFoundException() {
		assertThrows(DeckNotFoundException.class, () -> this.service.deleteDeck("WRONG_ID", "USER"));
	}

	@Test
	void deleteDeck_whenUserDoesNotExist_shouldThrowUserNotFoundException() {
		Mockito.when(this.deckRepository.findById("DECK_ID"))
			.thenReturn(Optional.of(this.user.getGameAcc().getDecks().get(0)));

		assertThrows(UserNotFoundException.class, () -> 
			this.service.deleteDeck("DECK_ID", "WRONG_USER")
		);
	}

	@Test
	void deleteDeck_whenDeckExist_shouldDeleteDeck() {
		Mockito.when(this.deckRepository.findById("DECK_ID"))
			.thenReturn(Optional.of(this.user.getGameAcc().getDecks().get(0)));

		this.service.deleteDeck("DECK_ID", "USER");

		Mockito.verify(this.deckRepository).delete(any());
		
		assertEquals(this.user.getGameAcc().getDecks().size(), 3);
	}

	@Test
	void addCard_whenUserDoesNotExist_shouldThrowUserNotFoundException() {
		assertThrows(UserNotFoundException.class, () -> 
			this.service.addCard("DECK_NAME", "CARD_ID", "WRONG_USER")
		);
	}

	@Test
	void addCard_whenDeckDoesNotExist_shouldThrowDeckNotFoundException() {
		Mockito.when(this.deckRepository.findByName("WRONG_DECK_NAME"))
			.thenReturn(Optional.of(new Deck() {{setName("WRONG_DECK_NAME");}}));
		
		Card card = new Card();
		card.setName("NEW_CARD");
		card.setId("NEW_CARD_ID");
		
		Mockito.when(this.cardRepository.findById("CARD_ID"))
			.thenReturn(Optional.of(card));
			
		assertThrows(DeckNotFoundException.class, () -> 
			this.service.addCard("WRONG_DECK_NAME", "CARD_ID", "USER")
		);
	}

	@Test
	void addCard_whenDeckIsFull_shouldThrowDeckSizeException() {
		Card card = new Card();
		card.setId("NEW_CARD_ID");
		card.setName("NEW_CARD");
		
		Mockito.when(this.cardRepository.findById("NEW_CARD_ID"))
			.thenReturn(Optional.of(card));
		
		this.user.getGameAcc().getDecks().get(0).getCards().add(new Card());
		
		assertThrows(DeckSizeException.class, () -> 
			this.service.addCard("DECK_NAME", "NEW_CARD_ID", "USER")
		);
	}

	@Test
	void addCard_whenCardDoesNotExist_shouldThrowCardNotFoundException() {
		Mockito.when(this.cardRepository.findById("WRONG_CARD_ID"))
			.thenReturn(Optional.empty());

		assertThrows(CardNotFoundException.class, () -> 
			this.service.addCard("DECK_NAME", "WRONG_CARD_ID", "USER")
		);
	}

	@Test
	void addCard_whenDeckHasTheCard_shouldThrowDeckContainsCardException() {
		Mockito.when(this.cardRepository.findById("CARD_ID")).thenReturn(Optional.of(new Card() {
			{
				setId("CARD_ID");
			}
		}));

		assertThrows(DeckContainsCardException.class, () -> this.service.addCard("DECK_NAME", "CARD_ID", "USER"));
	}

	@Test
	void addCard() {
		Card card = new Card();
		card.setId("NEW_CARD_ID");
		card.setName("NEW_CARD");

		Mockito.when(this.cardRepository.findById("NEW_CARD_ID")).thenReturn(Optional.of(card));

		Mockito.when(this.deckRepository.saveAndFlush(any()))
				.thenReturn(this.user.getGameAcc().getDecks().get(0));

		DeckServiceModel actual = this.service.addCard("DECK_NAME", "NEW_CARD_ID", "USER");

		assertEquals(this.user.getGameAcc().getDecks().get(0).getCards().size(), 5);
		assertEquals(this.user.getGameAcc().getDecks().get(0).getCards().size(), actual.getCards().size());
	}

	@Test
	void removeCard_whenDeckDoesNotExist_shouldThrowDeckNotFoudnException() {
		Mockito.when(this.deckRepository.findById("WORONG_DECK_ID")).thenReturn(Optional.empty());

		assertThrows(DeckNotFoundException.class, () -> this.service.removeCard("WORONG_DECK_ID", "CARD_NAME"));
	}

	@Test
	void removeCard_whenCardDoesNotExist_shouldThrowCardNotFoudnException() {
		Mockito.when(this.deckRepository.findById("DECK_ID")).thenReturn(Optional.of(new Deck()));

		Mockito.when(this.cardRepository.findByName("WRONG_CARD_NAME")).thenReturn(Optional.empty());

		assertThrows(CardNotFoundException.class, () -> this.service.removeCard("DECK_ID", "WRONG_CARD_NAME"));
	}

	@Test
	void testRemoveCard() {
		Deck deck = new Deck() {
			{
				setCards(new ArrayList<>() {
					{
						add(new Card() {
							{
								setId("CARD_ID2");
								setName("CARD_NAME2");
							}
						});
						add(new Card() {
							{
								setId("CARD_ID");
								setName("CARD_NAME");
							}
						});
					}
				});
			}
		};

		Mockito.when(this.deckRepository.findById("DECK_ID")).thenReturn(Optional.of(deck));

		Mockito.when(this.deckRepository.saveAndFlush(any())).thenReturn(deck);

		Mockito.when(this.cardRepository.findByName("CARD_NAME2"))
				.thenReturn(Optional.of(deck.getCards().stream().findFirst().get()));

		DeckServiceModel deckServiceModel = this.service.removeCard("DECK_ID", "CARD_NAME2");

		assertEquals(deck.getCards().size(), 1);
		assertEquals(deck.getCards().size(), deckServiceModel.getCards().size());
	}
}