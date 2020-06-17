package dny.apps.tiaw.validation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dny.apps.tiaw.domain.entities.Deck;
import dny.apps.tiaw.domain.entities.User;
import dny.apps.tiaw.domain.models.service.DeckCreateServiceModel;
import dny.apps.tiaw.error.user.UserNotFoundException;
import dny.apps.tiaw.repository.UserRepository;

@Service
public class DeckValidationServiceImpl implements DeckValidationService {

	private final UserRepository userRepository;
	
	@Autowired
	public DeckValidationServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@Override
	public boolean isValid(DeckCreateServiceModel deckCreateServiceModel, String username) {
		if(isNamePresented(deckCreateServiceModel.getName(), username) || isNameLengthInvalid(deckCreateServiceModel.getName())) {
			return false;
		}
		
		return true;
	}

	private boolean isNamePresented(String name, String username) {
		User user = this.userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException("User with given username does not exist!"));;
				
		for(Deck deck : user.getGameAcc().getDecks()) {
			if(deck.getName().equals(name)) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isNameLengthInvalid(String name) {
		return name.length() < 3 || name.length() > 10;
	}
}
