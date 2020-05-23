package dny.apps.tiaw.validation.card;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import dny.apps.tiaw.domain.models.binding.CardCreateBindingModel;
import dny.apps.tiaw.repository.CardRepository;
import dny.apps.tiaw.validation.ValidationConstants;
import dny.apps.tiaw.validation.annotation.Validator;

@Validator
public class CardCreateValidator implements org.springframework.validation.Validator {

	private final CardRepository cardRepository;
	
	@Autowired
	public CardCreateValidator(CardRepository cardRepository) {
		this.cardRepository = cardRepository;
	}
	
	@Override
	public boolean supports(Class<?> clazz) {
		return CardCreateBindingModel.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		CardCreateBindingModel cardCreateBindingModel = (CardCreateBindingModel) target;
		
		if(this.cardRepository.findByName(cardCreateBindingModel.getName()).isPresent()) {
			errors.reject(
					"name",
					String.format(ValidationConstants.CARD_NAME_ALREADY_EXISTS, cardCreateBindingModel.getName())
			);
		}
		
		if(cardCreateBindingModel.getName().length() < 3 || cardCreateBindingModel.getName().length() > 20) {
			errors.reject(
					"name",
					ValidationConstants.CARD_NAME_LENGTH
			);
		}
		
		if(cardCreateBindingModel.getPower() == null) {
			errors.reject(
					"power",
					ValidationConstants.POWER_MUST_BE_NOT_NULL
			);
		}
		
		if(cardCreateBindingModel.getPower() < 0) {
			errors.reject(
					"power",
					ValidationConstants.POWER_MUST_BE_POSITIVE
			);
		}
		
		if(cardCreateBindingModel.getDefense() == null) {
			errors.reject(
					"defense",
					ValidationConstants.DEFENSE_MUST_BE_NOT_NULL
			);
		}
		
		if(cardCreateBindingModel.getDefense() < 0) {
			errors.reject(
					"defense",
					ValidationConstants.DEFENSE_MUST_BE_POSITIVE
			);
		}
		
		if(cardCreateBindingModel.getImage() == null) {
			errors.reject(
					"image",
					ValidationConstants.DEFENSE_MUST_BE_POSITIVE
			);
		}
		
		if(cardCreateBindingModel.getRarity() == null) {
			errors.reject(
					"rarity",
					ValidationConstants.RARITY_MUST_BE_NOT_NULL
			);
		}
	}

}
