package dny.apps.tiaw.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
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
import dny.apps.tiaw.domain.entities.Rarity;
import dny.apps.tiaw.domain.models.service.CardServiceModel;
import dny.apps.tiaw.error.card.CardNotFoundException;
import dny.apps.tiaw.error.rarity.InvalidRarityException;
import dny.apps.tiaw.repository.CardRepository;

@SpringBootTest
@RunWith(SpringRunner.class)
class CardServiceImplTest {

	@Autowired
	private CardService service;
	
	@MockBean
	private CardRepository cardRepository;
	
	@Test
	void testFindById() {
		Optional<Card> card = Optional.of(new Card() {{
			setId("TEST_ID");
			setName("Card");
			setPower(10);
			setDefense(10);
			setPrice(10);
			setUrl("IMG_URL");
			setRarity(Rarity.Common);
		}});
		
		Mockito.when(cardRepository.findById("TEST_ID"))
			.thenReturn(card);
		
		CardServiceModel cardServiceModel = this.service.findById("TEST_ID");
		
		Exception nonExistentId = assertThrows(CardNotFoundException.class, ()-> {
			this.service.findById("WRONG_ID");
		});

		assertEquals(nonExistentId.getMessage(), "Card with given id does not exist!");
		
		assertEquals(card.get().getId(), cardServiceModel.getId());
		assertEquals(card.get().getName(), cardServiceModel.getName());
		assertEquals(card.get().getPower(), cardServiceModel.getPower());
		assertEquals(card.get().getDefense(), cardServiceModel.getDefense());
		assertEquals(card.get().getPrice(), cardServiceModel.getPrice());
		assertEquals(card.get().getUrl(), cardServiceModel.getUrl());
		assertEquals(card.get().getRarity(), cardServiceModel.getRarity());
	}

	@Test
	void testFindAllByRarity() {
		Optional<Card> card = Optional.of(new Card() {{
			setId("TEST_ID");
			setName("Card");
			setPower(10);
			setDefense(10);
			setPrice(10);
			setUrl("IMG_URL");
			setRarity(Rarity.Common);
		}});
		Optional<Card> card2 = Optional.of(new Card() {{
			setId("TEST_ID");
			setName("Card");
			setPower(10);
			setDefense(10);
			setPrice(10);
			setUrl("IMG_URL");
			setRarity(Rarity.Common);
		}});
		Optional<Card> card3 = Optional.of(new Card() {{
			setId("TEST_ID");
			setName("Card");
			setPower(10);
			setDefense(10);
			setPrice(10);
			setUrl("IMG_URL");
			setRarity(Rarity.Rare);
		}});
		Optional<Card> card4 = Optional.of(new Card() {{
			setId("TEST_ID");
			setName("Card");
			setPower(10);
			setDefense(10);
			setPrice(10);
			setUrl("IMG_URL");
			setRarity(Rarity.Uncommon);
		}});
		
		List<Card> cards = new ArrayList<>();
		cards.add(card.get());
		cards.add(card2.get());
		cards.add(card3.get());
		cards.add(card4.get());
		
		Exception nonExistentRarity = assertThrows(InvalidRarityException.class, ()-> {
			this.service.findAllByRarity("WRONG_RARITY");
		});

		assertEquals(nonExistentRarity.getMessage(), "Invalid rarity!");
		
		Mockito.when(this.cardRepository.findAll())
			.thenReturn(cards);
		
		List<CardServiceModel> actualCards = this.service.findAllByRarity("Common");
		
		assertTrue(actualCards.size() == 2);
		assertEquals(card.get().getName(), actualCards.get(0).getName());
		assertEquals(card2.get().getName(), actualCards.get(1).getName());
	}

	@Test
	void testFindAll() {
		Optional<Card> card = Optional.of(new Card() {{
			setId("TEST_ID");
			setName("Card");
			setPower(10);
			setDefense(10);
			setPrice(10);
			setUrl("IMG_URL");
			setRarity(Rarity.Common);
		}});
		Optional<Card> card2 = Optional.of(new Card() {{
			setId("TEST_ID");
			setName("Card");
			setPower(10);
			setDefense(10);
			setPrice(10);
			setUrl("IMG_URL");
			setRarity(Rarity.Common);
		}});
		Optional<Card> card3 = Optional.of(new Card() {{
			setId("TEST_ID");
			setName("Card");
			setPower(10);
			setDefense(10);
			setPrice(10);
			setUrl("IMG_URL");
			setRarity(Rarity.Rare);
		}});
		Optional<Card> card4 = Optional.of(new Card() {{
			setId("TEST_ID");
			setName("Card");
			setPower(10);
			setDefense(10);
			setPrice(10);
			setUrl("IMG_URL");
			setRarity(Rarity.Uncommon);
		}});
		
		List<Card> cards = new ArrayList<>();
		cards.add(card.get());
		cards.add(card2.get());
		cards.add(card3.get());
		cards.add(card4.get());
		
		Mockito.when(this.cardRepository.findAll())
			.thenReturn(cards);
		
		List<CardServiceModel> actualCards = this.service.findAll();
		
		assertEquals(cards.size(), actualCards.size());
		assertEquals(cards.get(0).getName(), actualCards.get(0).getName());
	}

	@Test
	void testCreateCard() {
		CardServiceModel cardServiceModel = new CardServiceModel() {{
			setName("Card01");
			setPower(10);
			setDefense(10);
			setUrl("IMG_URL");
			setRarity(Rarity.Common);
		}};
		
		Card card = new Card() {{
			setName("Card01");
			setPower(10);
			setDefense(10);
			setUrl("IMG_URL");
			setRarity(Rarity.Uncommon);
		}};
		
		this.service.setPrice(card);
		
		assertTrue(card.getPrice() == 30);
		
		Mockito.when(this.cardRepository.saveAndFlush(any()))
         	.thenReturn(card);
		
		CardServiceModel actualCard = this.service.createCard(cardServiceModel);
				
		assertEquals(cardServiceModel.getName(), actualCard.getName());
		assertEquals(actualCard.getPrice(), 30);
	}

	@Test
	void testUpdateCard() {
		CardServiceModel cardServiceModel = new CardServiceModel() {{
			setName("Card02");
			setPower(20);
			setDefense(20);
			setUrl("IMG_URL");
			setRarity(Rarity.Common);
		}};
		
		Optional<Card> card = Optional.of(new Card() {{
			setId("TEST_ID");
			setName("Card01");
			setPower(10);
			setDefense(10);
			setUrl("IMG_URL");
			setRarity(Rarity.Uncommon);
		}});
		
		Mockito.when(this.cardRepository.findById("TEST_ID"))
			.thenReturn(card);
		
		Mockito.when(this.cardRepository.saveAndFlush(any()))
 			.thenReturn(card.get());
		
		CardServiceModel actual = this.service.updateCard("TEST_ID", cardServiceModel);
		
		assertEquals(card.get().getName(), actual.getName());
	}

	@Test
	void testDeleteCard() {
		Optional<Card> card = Optional.of(new Card() {{
			setId("TEST_ID");
			setName("Card01");
			setPower(10);
			setDefense(10);
			setUrl("IMG_URL");
			setRarity(Rarity.Uncommon);
		}});
		
		Mockito.when(this.cardRepository.findById("TEST_ID"))
			.thenReturn(card);
		
		CardServiceModel  actual = this.service.deleteCard("TEST_ID");
		
		Mockito.verify(this.cardRepository, Mockito.times(1))
			.delete(card.get());
		
		assertEquals(card.get().getName(), actual.getName());
	}

}
