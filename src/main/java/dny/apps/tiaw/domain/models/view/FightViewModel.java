package dny.apps.tiaw.domain.models.view;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import dny.apps.tiaw.domain.models.service.GameAccServiceModel;

public class FightViewModel {
	private LocalDateTime fightTime;
	private GameAccServiceModel winner;
	private GameAccServiceModel loser;
	private Integer winnerWonPoints;
	private Integer loserLostPoints;
	
	public FightViewModel() {
		
	}
	
	public String getFightTime() {
		return this.fightTime.format(DateTimeFormatter.ofPattern("(yyyy.MM.dd) HH:mm:ss"));
	}
	public void setFightTime(LocalDateTime fightTime) {
		this.fightTime = fightTime;
	}
	public String getWinner() {
		return this.winner.getUsername();
	}
	public void setWinner(GameAccServiceModel winner) {
		this.winner = winner;
	}
	public String getLoser() {
		return this.loser.getUsername();
	}
	public void setLoser(GameAccServiceModel loser) {
		this.loser = loser;
	}
	public Integer getWinnerWonPoints() {
		return winnerWonPoints;
	}
	public void setWinnerWonPoints(Integer winnerWonPoints) {
		this.winnerWonPoints = winnerWonPoints;
	}
	public Integer getLoserLostPoints() {
		return loserLostPoints;
	}
	public void setLoserLostPoints(Integer loserLostPoints) {
		this.loserLostPoints = loserLostPoints;
	}
}
