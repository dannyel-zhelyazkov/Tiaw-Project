package dny.apps.tiaw.service;

import java.util.List;

import dny.apps.tiaw.domain.models.service.GameAccServiceModel;

public interface GameAccService {
	GameAccServiceModel findByUser(String username);
	
	List<GameAccServiceModel> findAllGameAccs();
	
	GameAccServiceModel buyCard(String cardId, String username);
	
	GameAccServiceModel addDeck(String deckId, String username);
	
	GameAccServiceModel setDefense(String deckId, String username);
	
	GameAccServiceModel setAttack(String deckId, String username);
	
	GameAccServiceModel removeDeck(String deck, String username);
	
	GameAccServiceModel fight(String user);
}
