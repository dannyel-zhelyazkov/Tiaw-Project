package dny.apps.tiaw.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import dny.apps.tiaw.domain.models.service.CardCreateServiceModel;
import dny.apps.tiaw.domain.models.service.CardEditServiceModel;
import dny.apps.tiaw.domain.models.service.CardServiceModel;

public interface CardService {
	CardServiceModel findById(String id);

	CardServiceModel findByName(String name);
	
	Page<CardServiceModel> findAllByOwner(PageRequest pageRequest, String owner);
	
	Page<CardServiceModel> findAllByRarity(PageRequest pageRequest, String rarity);
	
	Page<CardServiceModel> findAll(PageRequest pageRequest);
	
	List<CardServiceModel> findAll();

	CardServiceModel createCard(CardCreateServiceModel cardCreateServiceModel);

	CardServiceModel updateCard(String id, CardEditServiceModel cardEditServiceModel);

	CardServiceModel deleteCard(String id);
}