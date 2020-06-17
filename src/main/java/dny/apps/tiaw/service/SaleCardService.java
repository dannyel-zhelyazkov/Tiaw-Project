package dny.apps.tiaw.service;

import java.util.List;

import dny.apps.tiaw.domain.models.service.SaleCardServiceModel;

public interface SaleCardService {
	List<SaleCardServiceModel> findAll();
	
	List<SaleCardServiceModel> setForSale();
}
