package dny.apps.tiaw.validation.card;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import dny.apps.tiaw.domain.models.binding.CardEditBindingModel;
import dny.apps.tiaw.repository.CardRepository;
import dny.apps.tiaw.validation.ValidationConstants;
import dny.apps.tiaw.validation.annotation.Validator;

@Validator
public class CardEditValidator implements org.springframework.validation.Validator{

	private final CardRepository cardRepository;
	
	@Autowired
	public CardEditValidator(CardRepository cardRepository) {
		this.cardRepository = cardRepository;
	}
	
	@Override
	public boolean supports(Class<?> clazz) {
		return CardEditBindingModel.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		CardEditBindingModel cardEditBindingModel = (CardEditBindingModel) target;
		
		if(this.cardRepository.findByName(cardEditBindingModel.getName()).isPresent()) {
			errors.reject(
					"name",
					String.format(ValidationConstants.CARD_NAME_ALREADY_EXISTS, cardEditBindingModel.getName())
			);
		}
		
		if(cardEditBindingModel.getName().length() < 3 || cardEditBindingModel.getName().length() > 20) {
			errors.reject(
					"name",
					ValidationConstants.CARD_NAME_LENGTH
			);
		}
		
		if(cardEditBindingModel.getPower() == null) {
			errors.reject(
					"power",
					ValidationConstants.POWER_MUST_BE_NOT_NULL
			);
		}
		
		if(cardEditBindingModel.getPower() < 0) {
			errors.reject(
					"power",
					ValidationConstants.POWER_MUST_BE_POSITIVE
			);
		}
		
		if(cardEditBindingModel.getDefense() == null) {
			errors.reject(
					"defense",
					ValidationConstants.DEFENSE_MUST_BE_NOT_NULL
			);
		}
		
		if(cardEditBindingModel.getDefense() < 0) {
			errors.reject(
					"defense",
					ValidationConstants.DEFENSE_MUST_BE_POSITIVE
			);
		}
	}

}
