package dny.apps.tiaw.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

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
import dny.apps.tiaw.domain.entities.Rarity;
import dny.apps.tiaw.domain.models.service.CardCreateServiceModel;
import dny.apps.tiaw.domain.models.service.CardEditServiceModel;
import dny.apps.tiaw.domain.models.service.CardServiceModel;
import dny.apps.tiaw.error.card.CardNotFoundException;
import dny.apps.tiaw.error.card.InvalidCardCreateEditException;
import dny.apps.tiaw.error.rarity.RarityNotFoundException;
import dny.apps.tiaw.repository.CardRepository;

class CardServiceTest extends BaseServiceTest {

	@Autowired
	private CardService service;
	
	@MockBean
	private CardRepository cardRepository;
	
	private Card card;
	
	@BeforeEach
	public void setUp() {
		Mockito.when(this.cardRepository.findById("WRONG_CARD_ID"))
			.thenReturn(Optional.empty());
		
		card = new Card();
		
		Mockito.when(this.cardRepository.findById("CARD_ID"))
			.thenReturn(Optional.of(card));
	}
	
	@Test
	void findById_whenCardDoesNotExist_shoudlThrowCardNotFoundException() {
		assertThrows(CardNotFoundException.class, () -> 
			this.service.findById("WRONG_CARD_ID")
		);
	}
	
	@Test
	void findById_whenCardExists_shouldReturnCard() {		
		CardServiceModel actual = this.service.findById("CARD_ID");
		
		assertEquals(card.getName(), actual.getName());
	}

	@Test
	void findAllByRarity_whenRarityDoesNotExist_shouldThrowRarityNotFoundException() {
		assertThrows(RarityNotFoundException.class, () -> 
			this.service.findAllByRarity("RARITY")
		);
	}
	
	@Test
	void findAllByRarity_whenRarityExists_shouldReturnAllCard() {
		Card card1 = new Card() {{
			setRarity(Rarity.Common);
		}};
		Card card2 = new Card() {{
			setRarity(Rarity.Uncommon);
		}};
		Card card3 = new Card() {{
			setRarity(Rarity.Uncommon);
		}};
		Card card4 = new Card() {{
			setRarity(Rarity.Common);
		}};
		
		List<Card> cards = new ArrayList<>();
		cards.add(card1);
		cards.add(card2);
		cards.add(card3);
		cards.add(card4);
		
		Mockito.when(this.cardRepository.findAll())
			.thenReturn(cards);
		
		List<CardServiceModel> actual = this.service.findAllByRarity("Common");
		
		assertTrue(actual.size() == 2);
	}

	@Test
	void findAll() {
		Card card1 = new Card();
		Card card2 = new Card();
		Card card3 = new Card();
		
		List<Card> cards = new ArrayList<>();
		cards.add(card1);
		cards.add(card2);
		cards.add(card3);
		
		Mockito.when(this.cardRepository.findAll())
			.thenReturn(cards);
		
		List<CardServiceModel> actual = this.service.findAll();
		
		assertEquals(cards.size(), actual.size());
	}

	private CardCreateServiceModel notValidCardCreateServiceModel() {
		return new CardCreateServiceModel() {{
			setName("Na");
		}};
	}
	
	private CardCreateServiceModel validCardCreateServiceModel() {
		return new CardCreateServiceModel() {{
			setName("Card");
			setPower(10);
			setDefense(15);
			setUrl("Url");
			setRarity(Rarity.Common);
		}};
	}
	
	@Test
	void createCard_whenCardIsNotValid_shouldThrowInvalidCardCreateEditException() {
		CardCreateServiceModel cardCreateServiceModel = notValidCardCreateServiceModel();
		
		assertThrows(InvalidCardCreateEditException.class, () -> 
			this.service.createCard(cardCreateServiceModel)
		);
	}
	
	@Test
	void createCard_whenCardIsValid_shouldCreateCard() {
		CardCreateServiceModel cardCreateServiceModel = validCardCreateServiceModel();
		
		this.service.createCard(cardCreateServiceModel);
		
		ArgumentCaptor<Card> argument = ArgumentCaptor.forClass(Card.class);
		Mockito.verify(this.cardRepository).saveAndFlush(argument.capture());
		
		Card card = argument.getValue();
		
		assertNotNull(card);
		assertEquals(card.getName(), cardCreateServiceModel.getName());
		assertEquals(card.getPrice(), 10);
	}

	private CardEditServiceModel validCardEditServiceModel() {
		return new CardEditServiceModel() {{
			setName("Card");
			setPower(10);
			setDefense(10);
		}};
	}
	
	private CardEditServiceModel notValidCardEditServiceModel() {
		return new CardEditServiceModel() {{
			setName("C");
			setPower(10);
			setDefense(10);
		}};
	}
	
	@Test
	void updateCard_whenCardIsNotValid_shouldThrowInvalidCardCreateEditException() {
		CardEditServiceModel cardEditServiceModel = notValidCardEditServiceModel();
		
		Mockito.when(this.cardRepository.findById("WRONG_ID"))
			.thenReturn(Optional.of(new Card()));
		
		assertThrows(InvalidCardCreateEditException.class, () -> 
			this.service.updateCard("CARD_ID",cardEditServiceModel)
		);
	}
	
	@Test
	void updateCard_whenCardDoesNotExist_shouldThrowCardNotFoundException() {
		CardEditServiceModel cardEditServiceModel = validCardEditServiceModel();
		
		Mockito.when(this.cardRepository.findById("WRONG_ID"))
			.thenReturn(Optional.empty());
		
		assertThrows(CardNotFoundException.class, () -> 
			this.service.updateCard("WRONG_ID", cardEditServiceModel)
		);
	}
	
	@Test
	void testUpdateCard() {
		CardEditServiceModel cardEditServiceModel = validCardEditServiceModel();
				
		Mockito.when(this.cardRepository.saveAndFlush(any()))
			.thenReturn(card);
		
		this.service.updateCard("CARD_ID", cardEditServiceModel);
		
		assertNotNull(card);
		assertEquals(card.getName(), cardEditServiceModel.getName());
	}

	@Test
	void testDeleteCard() {		
		CardServiceModel  actual = this.service.deleteCard("CARD_ID");
		
		Mockito.verify(this.cardRepository, Mockito.times(1))
			.delete(card);
		
		assertEquals(card.getName(), actual.getName());
	}

}
