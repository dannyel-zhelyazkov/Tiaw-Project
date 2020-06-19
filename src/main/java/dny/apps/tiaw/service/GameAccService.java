package dny.apps.tiaw.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import dny.apps.tiaw.domain.models.service.GameAccServiceModel;

public interface GameAccService {
	GameAccServiceModel findByUser(String username);
	
	List<GameAccServiceModel> findAll();
	
	Page<GameAccServiceModel> findAllGameAccFight(PageRequest pageRequest);
	
	GameAccServiceModel buyCardById(String cardId, String username);
	
	GameAccServiceModel buyCardByName(String cardName, String username);
	
	GameAccServiceModel setDefenseDeck(String deckId, String username);
	
	GameAccServiceModel setAttackDeck(String deckId, String username);
	
	GameAccServiceModel wonFight(String defender, String attacker, String ubp, String ebp);
	
	GameAccServiceModel lostFight(String defender, String attacker, String ubp, String ebp);
	
	void resetAttackTickets();
}
