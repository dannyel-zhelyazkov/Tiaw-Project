package dny.apps.tiaw.validation.service;

import dny.apps.tiaw.domain.models.service.CardCreateServiceModel;
import dny.apps.tiaw.domain.models.service.CardEditServiceModel;

public interface CardValidationService {
	boolean isValid(CardCreateServiceModel cardCreateServiceModel);
	
	boolean isValid(CardEditServiceModel cardEditServiceModel);
}
