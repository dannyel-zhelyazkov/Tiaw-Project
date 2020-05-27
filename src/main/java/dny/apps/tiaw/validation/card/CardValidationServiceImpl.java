package dny.apps.tiaw.validation.card;

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
		return 	isNamePresented(cardCreateServiceModel.getName()) &&
				nameLength(cardCreateServiceModel.getName()) &&
				powerZeroPositive(cardCreateServiceModel.getPower()) && 
				defenseZeroPositive(cardCreateServiceModel.getDefense()) && 
				urlNotNull(cardCreateServiceModel.getUrl()) &&
				rarityNotNull(cardCreateServiceModel.getRarity());
	}

	@Override
	public boolean isValid(CardEditServiceModel cardEditServiceModel) {
		return  isNamePresented(cardEditServiceModel.getName()) &&
				nameLength(cardEditServiceModel.getName()) &&
				powerZeroPositive(cardEditServiceModel.getPower()) && 
				defenseZeroPositive(cardEditServiceModel.getDefense());
	}
	
	private boolean nameLength(String name) {
		return name.length() >= 3 && name.length() <= 20;
	}
	
	private boolean isNamePresented(String name) {
		return !this.cardRepository.findByName(name).isPresent();
	}
	
	private boolean powerZeroPositive(Integer power) {
		return power >= 0;
	}
	
	private boolean defenseZeroPositive(Integer defense) {
		return defense >= 0;
	}
	
	private boolean urlNotNull(String url) {
		return url != null;
	}
	
	private boolean rarityNotNull(Rarity rarity) {
		return rarity != null;
	}
}
