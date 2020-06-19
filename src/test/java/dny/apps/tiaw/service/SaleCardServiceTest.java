package dny.apps.tiaw.service;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import dny.apps.tiaw.domain.entities.Card;
import dny.apps.tiaw.domain.entities.SaleCard;
import dny.apps.tiaw.domain.models.service.SaleCardServiceModel;
import dny.apps.tiaw.repository.CardRepository;
import dny.apps.tiaw.repository.SaleCardRepository;

class SaleCardServiceTest extends BaseServiceTest {

	@Autowired
	private SaleCardService saleCardService;
	
	@MockBean
	private SaleCardRepository saleCardRepository;
	
	@MockBean
	private CardRepository cardRepository;
	
	@Test
	void testFindAll() {
		SaleCard saleCard = new SaleCard();
		SaleCard saleCard1 = new SaleCard();
		SaleCard saleCard2 = new SaleCard();
		
		List<SaleCard> saleCards = new ArrayList<>();
		saleCards.add(saleCard);
		saleCards.add(saleCard1);
		saleCards.add(saleCard2);
		
		Mockito.when(this.saleCardRepository.findAll())
			.thenReturn(saleCards);
		
		List<SaleCardServiceModel> actualSaleCards = this.saleCardService.findAll();
		
		assertEquals(saleCards.size(), actualSaleCards.size());
	}

	@Test
	void testSetForSale() {
		Card card = new Card();
		card.setName("CARD");
		card.setPrice(10);
		Card card1 = new Card();
		card1.setName("CARD1");
		card1.setPrice(30);
		Card card2 = new Card();
		card2.setName("CARD2");
		card2.setPrice(10);
		Card card3 = new Card();
		card3.setName("CARD3");
		card3.setPrice(150);
		Card card4 = new Card();
		card4.setName("CARD4");
		card4.setPrice(10);
		Card card5 = new Card();
		card5.setName("CARD5");
		card5.setPrice(1);
		
		List<Card> cards = new ArrayList<>();
		cards.add(card);
		cards.add(card1);
		cards.add(card2);
		cards.add(card3);
		cards.add(card4);
		cards.add(card5);
		
		Mockito.when(this.cardRepository.findAll())
			.thenReturn(cards);
		
		List<SaleCardServiceModel> actualSaleCards = this.saleCardService.setOnSale();
		
		assertEquals(actualSaleCards.size(), 4);
		assertTrue(actualSaleCards.get(0).getPrice() >= actualSaleCards.get(1).getPrice() 
						&& actualSaleCards.get(1).getPrice() >= actualSaleCards.get(2).getPrice()
						&& actualSaleCards.get(2).getPrice() >= actualSaleCards.get(3).getPrice());
	}
}
