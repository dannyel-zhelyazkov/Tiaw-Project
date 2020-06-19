package dny.apps.tiaw.validation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import dny.apps.tiaw.domain.models.binding.CardCreateBindingModel;
import dny.apps.tiaw.repository.CardRepository;
import dny.apps.tiaw.validation.ValidationConstants;
import dny.apps.tiaw.validation.annotation.Validator;

@Validator
public class CardCreateValidaor implements org.springframework.validation.Validator {
	
	private final CardRepository cardRepository;
	
	@Autowired
	public CardCreateValidaor(CardRepository cardRepository) {
		this.cardRepository = cardRepository;
	}

	@Override
	public boolean supports(Class<?> aClass) {
		return CardCreateBindingModel.class.equals(aClass);
	}

	@Override
	public void validate(Object target, Errors errors) {
		CardCreateBindingModel cardCreateBindingModel = (CardCreateBindingModel) target;
		
		if(this.cardRepository.findByName(cardCreateBindingModel.getName()).isPresent()) {
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
		
		if(cardCreateBindingModel.getImage() == null || cardCreateBindingModel.getImage().getOriginalFilename().equals("")) {
			errors.rejectValue("image", 
					ValidationConstants.INVALID_IMAGE,
					ValidationConstants.INVALID_IMAGE
			);
		}
		
		if(cardCreateBindingModel.getRarity() == null) {
			errors.rejectValue("rarity", 
					ValidationConstants.INVALID_RARITY,
					ValidationConstants.INVALID_RARITY
			);
		}
	}
}
