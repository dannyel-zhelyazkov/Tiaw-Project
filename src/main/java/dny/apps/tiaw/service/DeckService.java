package dny.apps.tiaw.service;

import java.util.Set;

import dny.apps.tiaw.domain.models.service.CardServiceModel;
import dny.apps.tiaw.domain.models.service.DeckServiceModel;

public interface DeckService {
	DeckServiceModel findById(String id);
	
	DeckServiceModel findByOwner(String username, String deck);

	DeckServiceModel findDefenseDeckByOwner(String owner);

	Set<DeckServiceModel> findAllDecksByOwner(String owner);
	
	DeckServiceModel createDeck(DeckServiceModel deckServiceModel);
	
	DeckServiceModel deleteDeck(String id);
	
	DeckServiceModel addCard(String deckName, String cardId, String username);
	
	DeckServiceModel removeCard(DeckServiceModel deckServiceModel, CardServiceModel cardServiceModel);
}
