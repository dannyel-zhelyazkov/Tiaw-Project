package dny.apps.tiaw.validation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dny.apps.tiaw.domain.entities.Rarity;
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
		if(isNamePresented(cardCreateServiceModel.getName(), "") ||
				isNameLengthInvalid(cardCreateServiceModel.getName()) ||
				isPowerNegative(cardCreateServiceModel.getPower()) || 
				isDefenseNegative(cardCreateServiceModel.getDefense()) || 
				isUrlNull(cardCreateServiceModel.getUrl()) ||
				isRarityNull(cardCreateServiceModel.getRarity())) {
			return false;
		}
		
		return true;
	}

	@Override
	public boolean isValid(CardEditServiceModel cardEditServiceModel) {
		if(isNamePresented(cardEditServiceModel.getName(), cardEditServiceModel.getOldName()) ||
				isNameLengthInvalid(cardEditServiceModel.getName()) ||
				isPowerNegative(cardEditServiceModel.getPower()) ||
				isDefenseNegative(cardEditServiceModel.getDefense())) {
			return false;
		}
		
		return true;
	}
	
	private boolean isNameLengthInvalid(String name) {
		return name.length() < 3 || name.length() > 20;
	}
	
	private boolean isNamePresented(String name, String oldName) {
		return this.cardRepository.findByName(name).isPresent() && !name.equals(oldName);
	}
	
	private boolean isPowerNegative(Integer power) {
		return power < 0 || power == null;
	}
	
	private boolean isDefenseNegative(Integer defense) {
		return defense < 0 || defense == null;
	}
	
	private boolean isUrlNull(String url) {
		return url == null;
	}
	
	private boolean isRarityNull(Rarity rarity) {
		return rarity == null;
	}
}
