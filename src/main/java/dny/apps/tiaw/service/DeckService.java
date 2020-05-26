package dny.apps.tiaw.service;

import java.util.Set;

import dny.apps.tiaw.domain.models.service.DeckCreateServiceModel;
import dny.apps.tiaw.domain.models.service.DeckServiceModel;

public interface DeckService {
	DeckServiceModel findById(String id);
	
	DeckServiceModel findByOwner(String username, String deck);

	DeckServiceModel findDefenseDeckByOwner(String owner);

	Set<DeckServiceModel> findAllDecksByOwner(String owner);
	
	DeckServiceModel createDeck(DeckCreateServiceModel deckCreateServiceModel, String username);
	
	DeckServiceModel deleteDeck(String id, String username);
	
	DeckServiceModel addCard(String deckName, String cardId, String username);
	
	DeckServiceModel removeCard(String deckId, String cardName);
}
