package dny.apps.tiaw.validation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import dny.apps.tiaw.domain.models.binding.CardEditBindingModel;
import dny.apps.tiaw.repository.CardRepository;
import dny.apps.tiaw.validation.ValidationConstants;
import dny.apps.tiaw.validation.annotation.Validator;

@Validator
public class CardEditValidator implements org.springframework.validation.Validator {
	
	private final CardRepository cardRepository;
	
	@Autowired
	public CardEditValidator(CardRepository cardRepository) {
		this.cardRepository = cardRepository;
	}

	@Override
	public boolean supports(Class<?> aClass) {
		return CardEditBindingModel.class.equals(aClass);
	}

	@Override
	public void validate(Object target, Errors errors) {
		CardEditBindingModel cardCreateBindingModel = (CardEditBindingModel) target;
		
		if(this.cardRepository.findByName(cardCreateBindingModel.getName()).isPresent() 
				&& !cardCreateBindingModel.getName().equals(cardCreateBindingModel.getOldName())) {
			errors.rejectValue("name", 
					String.format(ValidationConstants.CARD_NAME_ALREADY_EXISTS, cardCreateBindingModel.getName()),
					String.format(ValidationConstants.CARD_NAME_ALREADY_EXISTS, cardCreateBindingModel.getName())
			);
		}
		
		if(cardCreateBindingModel.getName().length() < 3 || cardCreateBindingModel.getName().length() > 20) {
			errors.rejectValue("name",
					ValidationConstants.INVALID_CARD_NAME,
					ValidationConstants.INVALID_CARD_NAME
			);
		}
		
		if(cardCreateBindingModel.getPower() == null || cardCreateBindingModel.getPower() < 0) {
			errors.rejectValue("power", 
					ValidationConstants.INVALID_CARD_POWER,
					ValidationConstants.INVALID_CARD_POWER
			);
		}
		
		if(cardCreateBindingModel.getDefense() == null || cardCreateBindingModel.getDefense() < 0) {
			errors.rejectValue("defense", 
					ValidationConstants.INVALID_CARD_DEFENSE,
					ValidationConstants.INVALID_CARD_DEFENSE
			);
		}
	}
}
