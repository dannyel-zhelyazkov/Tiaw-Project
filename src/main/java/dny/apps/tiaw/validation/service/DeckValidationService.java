package dny.apps.tiaw.validation.service;

import dny.apps.tiaw.domain.models.service.DeckCreateServiceModel;

public interface DeckValidationService {
	boolean isValid(DeckCreateServiceModel deckCreateServiceModel, String username);
}
