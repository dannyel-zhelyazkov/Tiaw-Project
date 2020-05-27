package dny.apps.tiaw.validation.deck;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import dny.apps.tiaw.domain.entities.Deck;
import dny.apps.tiaw.domain.entities.GameAcc;
import dny.apps.tiaw.domain.entities.User;
import dny.apps.tiaw.domain.models.service.DeckCreateServiceModel;
import dny.apps.tiaw.error.user.UserNotFoundException;
import dny.apps.tiaw.repository.UserRepository;
import dny.apps.tiaw.service.BaseServiceTest;

class DeckValidationServiceTest extends BaseServiceTest {
	
	@Autowired
	private DeckValidationService deckValidationService;
	
	@MockBean
	private UserRepository userRepository;
	
	@BeforeEach
	private void setUp() {
		Mockito.when(this.userRepository.findByUsername("USER"))
		.thenReturn(Optional.of(new User() {{
			setGameAcc(new GameAcc() {{
				setDecks(new ArrayList<>());
			}});
		}}));
	}
	
	@Test
	void isValid_whenUserDoesNotExist_shouldThrowUserNotFoundException() {
		Mockito.when(this.userRepository.findByUsername("WRONG_USER"))
			.thenReturn(Optional.empty());
		
		DeckCreateServiceModel deckCreateServiceModel = new DeckCreateServiceModel();
		deckCreateServiceModel.setName("DECK");
		
		assertThrows(UserNotFoundException.class, () ->
			this.deckValidationService.isValid(deckCreateServiceModel, "WRONG_USER")
		);
	}
	
	@Test
	void isValid_whenDeckExist_shouldReturnFalse() {
		DeckCreateServiceModel deckCreateServiceModel = new DeckCreateServiceModel();
		deckCreateServiceModel.setName("DECK");
		
		Mockito.when(this.userRepository.findByUsername("USER"))
			.thenReturn(Optional.of(new User() {{
				setGameAcc(new GameAcc() {{
					setDecks(new ArrayList<>() {{
						add(new Deck() {{
							setName("DECK");
						}});
					}});
				}});
			}}));
			
		assertFalse(this.deckValidationService.isValid(deckCreateServiceModel, "USER"));
	}

	@Test
	void isValid_whenNameIsTooShort_shouldReturnFalse() {
		DeckCreateServiceModel deckCreateServiceModel = new DeckCreateServiceModel();
		deckCreateServiceModel.setName("DE");
			
		assertFalse(this.deckValidationService.isValid(deckCreateServiceModel, "USER"));
	}
	
	@Test
	void isValid_whenNameIsTooLong_shouldReturnFalse() {
		DeckCreateServiceModel deckCreateServiceModel = new DeckCreateServiceModel();
		deckCreateServiceModel.setName("DECK_NAME_IS_TOO_LONG");
			
		assertFalse(this.deckValidationService.isValid(deckCreateServiceModel, "USER"));
	}
	
	@Test
	void isValid_whenAllIsValid_shouldReturnTrue() {
		DeckCreateServiceModel deckCreateServiceModel = new DeckCreateServiceModel();
		deckCreateServiceModel.setName("DECK");
			
		assertTrue(this.deckValidationService.isValid(deckCreateServiceModel, "USER"));
	}
}
