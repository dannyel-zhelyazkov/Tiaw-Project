package dny.apps.tiaw.validation.deck;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dny.apps.tiaw.domain.models.service.DeckCreateServiceModel;
import dny.apps.tiaw.repository.DeckRepository;

@Service
public class DeckValidationServiceImpl implements DeckValidationService {

	private final DeckRepository dekcRepository;
	
	@Autowired
	public DeckValidationServiceImpl(DeckRepository dekcRepository) {
		this.dekcRepository = dekcRepository;
	}
	
	@Override
	public boolean isValid(DeckCreateServiceModel deckCreateServiceModel) {
		if(this.dekcRepository.findByName(deckCreateServiceModel.getName()).isPresent() || 
				deckCreateServiceModel.getName().length() < 3 || 
				deckCreateServiceModel.getName().length() > 10) {
			return false;
		}
		
		return true;
	}

}
