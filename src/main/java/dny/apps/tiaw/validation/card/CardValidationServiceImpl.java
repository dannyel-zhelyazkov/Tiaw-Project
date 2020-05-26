package dny.apps.tiaw.validation.card;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dny.apps.tiaw.domain.models.service.CardCreateServiceModel;
import dny.apps.tiaw.domain.models.service.CardEditServiceModel;
import dny.apps.tiaw.repository.CardRepository;

@Service
public class CardValidationServiceImpl implements CardValidationService {

	private final CardRepository cardRepository;
	
	@Autowired
	public CardValidationServiceImpl(CardRepository cardRepository) {
		this.cardRepository = cardRepository;
	}
	
	@Override
	public boolean isValid(CardCreateServiceModel cardCreateServiceModel) {
		return !this.cardRepository.findByName(cardCreateServiceModel.getName()).isPresent() &&
				cardCreateServiceModel.getName().length() >= 3 && 
				cardCreateServiceModel.getName().length() <= 20 &&
				cardCreateServiceModel.getPower() >= 0 && 
				cardCreateServiceModel.getDefense() >= 0 && 
				cardCreateServiceModel.getUrl() != null &&
				cardCreateServiceModel.getRarity() != null;
	}

	@Override
	public boolean isValid(CardEditServiceModel cardEditServiceModel) {
		return !this.cardRepository.findByName(cardEditServiceModel.getName()).isPresent() &&
			cardEditServiceModel.getName().length() >= 3 && 
			cardEditServiceModel.getName().length() <= 20 &&
			cardEditServiceModel.getPower() >= 0 && 
			cardEditServiceModel.getDefense() >= 0;
	}
}
