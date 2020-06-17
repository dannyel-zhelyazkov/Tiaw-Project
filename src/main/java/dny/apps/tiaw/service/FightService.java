package dny.apps.tiaw.service;

import java.util.List;

import dny.apps.tiaw.domain.models.service.FightServiceModel;

public interface FightService {
	List<FightServiceModel> findAllByUser(String username);
	
	FightServiceModel registerFight(String winner, String loser, Integer winnerWonPoints, Integer loserLostPoints);
}
