package dny.apps.tiaw.validation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import dny.apps.tiaw.domain.entities.User;
import dny.apps.tiaw.domain.models.binding.DeckAddBindingModel;
import dny.apps.tiaw.repository.UserRepository;
import dny.apps.tiaw.validation.ValidationConstants;
import dny.apps.tiaw.validation.annotation.Validator;

@Validator
public class DeckCreateValidator implements org.springframework.validation.Validator {
	
	private final UserRepository userRepository;
	
	@Autowired
	public DeckCreateValidator(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public boolean supports(Class<?> aClass) {
		return DeckAddBindingModel.class.equals(aClass);
	}

	@Override
	public void validate(Object target, Errors errors) {
		DeckAddBindingModel deckAddBindingModel = (DeckAddBindingModel) target;
		
		User user = this.userRepository.findByUsername(deckAddBindingModel.getUsername()).get();
				
		for(int  i = 0; i < user.getGameAcc().getDecks().size(); i++) {
			if(user.getGameAcc().getDecks().get(i).getName().equals(deckAddBindingModel.getName())) {
				errors.rejectValue("name",
						String.format(ValidationConstants.DECK_NAME_ALREADY_EXISTS, deckAddBindingModel.getName()),
						String.format(ValidationConstants.DECK_NAME_ALREADY_EXISTS, deckAddBindingModel.getName())
				);
			}
		}
		
		if(deckAddBindingModel.getName().length() < 3 || deckAddBindingModel.getName().length() > 10) {
			errors.rejectValue("name",
					ValidationConstants.INVALID_DECK_NAME,
					ValidationConstants.INVALID_DECK_NAME
			);
		}
	
		if(user.getGameAcc().getDecks().size() == 5) {
			errors.rejectValue("name",
					ValidationConstants.DECK_SIZE_IS_FULL,
					ValidationConstants.DECK_SIZE_IS_FULL
			);
		}
	}
}
