package dny.apps.tiaw.service;

import java.util.List;

import dny.apps.tiaw.domain.entities.Card;
import dny.apps.tiaw.domain.models.service.CardServiceModel;

public interface CardService {
	CardServiceModel findById(String id);

	List<CardServiceModel> findAllByRarity(String rarity);

	List<CardServiceModel> findAll();

	CardServiceModel createCard(CardServiceModel cardServiceModel);

	CardServiceModel updateCard(String id, CardServiceModel cardServiceModel);

	CardServiceModel deleteCard(String id);

	void setPrice(Card card);
}