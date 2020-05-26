package dny.apps.tiaw.service;

import java.util.List;

import dny.apps.tiaw.domain.entities.Card;
import dny.apps.tiaw.domain.models.service.CardCreateServiceModel;
import dny.apps.tiaw.domain.models.service.CardEditServiceModel;
import dny.apps.tiaw.domain.models.service.CardServiceModel;

public interface CardService {
	CardServiceModel findById(String id);

	List<CardServiceModel> findAllByRarity(String rarity);

	List<CardServiceModel> findAll();

	CardServiceModel createCard(CardCreateServiceModel cardCreateServiceModel);

	CardServiceModel updateCard(String id, CardEditServiceModel cardEditServiceModel);

	CardServiceModel deleteCard(String id);
}