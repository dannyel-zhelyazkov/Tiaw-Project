package dny.apps.tiaw.validation.card;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import dny.apps.tiaw.domain.entities.Card;
import dny.apps.tiaw.domain.entities.Rarity;
import dny.apps.tiaw.domain.models.service.CardCreateServiceModel;
import dny.apps.tiaw.domain.models.service.CardEditServiceModel;
import dny.apps.tiaw.repository.CardRepository;
import dny.apps.tiaw.service.BaseServiceTest;
import dny.apps.tiaw.validation.service.CardValidationService;

class CardValidationServiceTest extends BaseServiceTest {

	@Autowired
	private CardValidationService cardValidationService;
	
	@MockBean
	private CardRepository cardRepository;
	
	@Test
	void isValidCardCreateServiceModel_whenCardExist_shouldReturnFalse() {
		CardCreateServiceModel cardCreateServiceModel = new CardCreateServiceModel();
		cardCreateServiceModel.setName("NAME");
		
		Mockito.when(this.cardRepository.findByName("NAME"))
			.thenReturn(Optional.of(new Card() {{
				setName("NAME");
			}}));
		
		assertFalse(this.cardValidationService.isValid(cardCreateServiceModel));
	}
	
	@Test
	void isValidCardCreateServiceModel_whenNameIsTooShort_shouldReturnFalse() {
		CardCreateServiceModel cardCreateServiceModel = new CardCreateServiceModel();
		cardCreateServiceModel.setName("NA");
		
		assertFalse(this.cardValidationService.isValid(cardCreateServiceModel));
	}
	
	@Test
	void isValidCardCreateServiceModel_whenNameIsTooLong_shouldReturnFalse() {
		CardCreateServiceModel cardCreateServiceModel = new CardCreateServiceModel();
		cardCreateServiceModel.setName("NAME_FOR_THIS_CARD_ENTITY_IS_TOO_LONG");
		
		assertFalse(this.cardValidationService.isValid(cardCreateServiceModel));
	}
	
	@Test
	void isValidCardCreateServiceModel_whenPowerIsNegative_shouldReturnFalse() {
		CardCreateServiceModel cardCreateServiceModel = new CardCreateServiceModel();
		cardCreateServiceModel.setName("NAME");
		cardCreateServiceModel.setPower(-10);
		
		assertFalse(this.cardValidationService.isValid(cardCreateServiceModel));
	}
	
	@Test
	void isValidCardCreateServiceModel_whenDefenseIsNegative_shouldReturnFalse() {
		CardCreateServiceModel cardCreateServiceModel = new CardCreateServiceModel();
		cardCreateServiceModel.setName("NAME");
		cardCreateServiceModel.setPower(10);
		cardCreateServiceModel.setDefense(-10);
		
		assertFalse(this.cardValidationService.isValid(cardCreateServiceModel));
	}
	
	@Test
	void isValidCardCreateServiceModel_whenUrlIsNull_shouldReturnFalse() {
		CardCreateServiceModel cardCreateServiceModel = new CardCreateServiceModel();
		cardCreateServiceModel.setName("NAME");
		cardCreateServiceModel.setPower(10);
		cardCreateServiceModel.setDefense(10);
		cardCreateServiceModel.setUrl(null);
		
		assertFalse(this.cardValidationService.isValid(cardCreateServiceModel));
	}
	
	@Test
	void isValidCardCreateServiceModel_whenRarityIsNull_shouldReturnFalse() {
		CardCreateServiceModel cardCreateServiceModel = new CardCreateServiceModel();
		cardCreateServiceModel.setName("NAME");
		cardCreateServiceModel.setPower(10);
		cardCreateServiceModel.setDefense(10);
		cardCreateServiceModel.setUrl("URL");
		cardCreateServiceModel.setRarity(null);
		
		assertFalse(this.cardValidationService.isValid(cardCreateServiceModel));
	}

	@Test
	void isValidCardCreateServiceModel_whenAllIsValid_shouldReturnTrue() {
		CardCreateServiceModel cardCreateServiceModel = new CardCreateServiceModel();
		cardCreateServiceModel.setName("NAME");
		cardCreateServiceModel.setPower(10);
		cardCreateServiceModel.setDefense(10);
		cardCreateServiceModel.setUrl("URL");
		cardCreateServiceModel.setRarity(Rarity.Common);
		
		assertTrue(this.cardValidationService.isValid(cardCreateServiceModel));
	}
	
	@Test
	void isValidCardEditServiceModel_whenCardExist_shouldReturnFalse() {
		CardEditServiceModel cardEditServiceModel = new CardEditServiceModel();
		cardEditServiceModel.setName("NAME");
		
		Mockito.when(this.cardRepository.findByName("NAME"))
			.thenReturn(Optional.of(new Card() {{
				setName("NAME");
			}}));
		
		assertFalse(this.cardValidationService.isValid(cardEditServiceModel));
	}
	
	@Test
	void isValidCardEditServiceModel_whenNameIsTooShort_shouldReturnFalse() {
		CardEditServiceModel cardEditServiceModel = new CardEditServiceModel();
		cardEditServiceModel.setName("NA");
		
		assertFalse(this.cardValidationService.isValid(cardEditServiceModel));
	}
	
	@Test
	void isValidCardEditServiceModel_whenNameIsTooLong_shouldReturnFalse() {
		CardEditServiceModel cardEditServiceModel = new CardEditServiceModel();
		cardEditServiceModel.setName("NAME_FOR_THIS_CARD_ENTITY_IS_TOO_LONG");
		
		assertFalse(this.cardValidationService.isValid(cardEditServiceModel));
	}
	
	@Test
	void isValidCardEditServiceModel_whenPowerIsNegative_shouldReturnFalse() {
		CardEditServiceModel cardEditServiceModel = new CardEditServiceModel();
		cardEditServiceModel.setName("NAME");
		cardEditServiceModel.setPower(-10);
		
		assertFalse(this.cardValidationService.isValid(cardEditServiceModel));
	}
	
	@Test
	void isValidCardEditServiceModel_whenDefenseIsNegative_shouldReturnFalse() {
		CardEditServiceModel cardEditServiceModel = new CardEditServiceModel();
		cardEditServiceModel.setName("NAME");
		cardEditServiceModel.setPower(10);
		cardEditServiceModel.setDefense(-10);
		
		assertFalse(this.cardValidationService.isValid(cardEditServiceModel));
	}
	
	@Test
	void isValidCardEditServiceModel_whenAllIsValid_shouldReturnTrue() {
		CardEditServiceModel cardEditServiceModel = new CardEditServiceModel();
		cardEditServiceModel.setName("NAME");
		cardEditServiceModel.setPower(10);
		cardEditServiceModel.setDefense(10);
		
		assertTrue(this.cardValidationService.isValid(cardEditServiceModel));
	}
}
