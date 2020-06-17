package dny.apps.tiaw.validation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import dny.apps.tiaw.domain.models.binding.CardSearchBindingModel;
import dny.apps.tiaw.repository.CardRepository;
import dny.apps.tiaw.validation.ValidationConstants;
import dny.apps.tiaw.validation.annotation.Validator;

@Validator
public class CardSearchValidator implements org.springframework.validation.Validator {

	private final CardRepository cardRepository;
	
	@Autowired
	public CardSearchValidator(CardRepository cardRepository) {
		this.cardRepository = cardRepository;
	}
	
	@Override
	public boolean supports(Class<?> aClass) {
		return CardSearchBindingModel.class.equals(aClass);
	}

	@Override
	public void validate(Object target, Errors errors) {
		CardSearchBindingModel cardSearchBindingModel = (CardSearchBindingModel) target;
		
		if(cardSearchBindingModel.getName().length() == 0) {
			errors.rejectValue("name",
					ValidationConstants.EMPTY_CARD_NAME,
					ValidationConstants.EMPTY_CARD_NAME
			);
		}
		
		if(!this.cardRepository.findByName(cardSearchBindingModel.getName()).isPresent() && cardSearchBindingModel.getName().length() > 0) {
			errors.rejectValue("name",
					String.format(ValidationConstants.CARD_NAME_DOES_NOT_EXISTS, cardSearchBindingModel.getName()),
					String.format(ValidationConstants.CARD_NAME_DOES_NOT_EXISTS, cardSearchBindingModel.getName())
			);
		}
	}
}
