package dny.apps.tiaw.validation.deck;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import dny.apps.tiaw.domain.models.binding.DeckAddBindingModel;
import dny.apps.tiaw.repository.DeckRepository;
import dny.apps.tiaw.validation.ValidationConstants;
import dny.apps.tiaw.validation.annotation.Validator;

@Validator
public class DeckCreateValidatior implements org.springframework.validation.Validator{

	private final DeckRepository deckRepository;
	
	@Autowired
	public DeckCreateValidatior(DeckRepository deckRepository) {
		this.deckRepository = deckRepository;
	}
	
	@Override
	public boolean supports(Class<?> clazz) {
		return DeckAddBindingModel.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		DeckAddBindingModel deckAddBindingModel = (DeckAddBindingModel) target;
		
		if(deckAddBindingModel.getName().length() < 3 || deckAddBindingModel.getName().length() > 10) {
			errors.reject(
					"name",
					ValidationConstants.DECK_NAME_LENGTH
			);
		}
	}

}
