package dny.apps.tiaw.domain.models.service;

import java.time.LocalDateTime;

public class FightServiceModel {
	private LocalDateTime fightTime;
	private GameAccServiceModel winner;
	private GameAccServiceModel loser;
	private Integer winnerWonPoints;
	private Integer loserLostPoints;
	
	public FightServiceModel() {}
	
	public LocalDateTime getFightTime() {
		return fightTime;
	}
	public void setFightTime(LocalDateTime fightTime) {
		this.fightTime = fightTime;
	}
	public GameAccServiceModel getWinner() {
		return winner;
	}
	public void setWinner(GameAccServiceModel winner) {
		this.winner = winner;
	}
	public GameAccServiceModel getLoser() {
		return loser;
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
