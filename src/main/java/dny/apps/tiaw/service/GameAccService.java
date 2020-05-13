package dny.apps.tiaw.service;

import java.util.List;

import dny.apps.tiaw.domain.models.service.GameAccServiceModel;

public interface GameAccService {
	GameAccServiceModel findByUser(String username);
	
	List<GameAccServiceModel> findAll();
	
	List<GameAccServiceModel> findAllFightGameAccs();
	
	GameAccServiceModel buyCard(String cardId, String username);
	
	GameAccServiceModel addDeck(String deckId, String username);
	
	GameAccServiceModel setDefense(String deckId, String username);
	
	GameAccServiceModel setAttack(String deckId, String username);
	
	GameAccServiceModel removeDeck(String deck, String username);
	
	GameAccServiceModel wonFight(String defender, String attacker, String ubp, String ebp);
	
	GameAccServiceModel lostFight(String defender, String attacker, String ubp, String ebp);
	
	void resetAttackTickets();
}
