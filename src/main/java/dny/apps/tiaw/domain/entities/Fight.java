package dny.apps.tiaw.domain.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "fights")
public class Fight extends BaseEntity {
	private LocalDateTime fightTime;
	private GameAcc winner;
	private GameAcc loser;
	private Integer winnerWonPoints;
	private Integer loserLostPoints;
	
	public Fight() {}

	@Column(name="fight_time", nullable = false, updatable = false, unique = false)
	public LocalDateTime getFightTime() {
		return fightTime;
	}
	public void setFightTime(LocalDateTime fightTime) {
		this.fightTime = fightTime;
	}
	@OneToOne
	public GameAcc getWinner() {
		return winner;
	}
	public void setWinner(GameAcc winner) {
		this.winner = winner;
	}
	@OneToOne
	public GameAcc getLoser() {
		return loser;
	}
	public void setLoser(GameAcc loser) {
		this.loser = loser;
	}
	@Column(name="winner_won_points", nullable = false, updatable = false, unique = false)
	public Integer getWinnerWonPoints() {
		return winnerWonPoints;
	}
	public void setWinnerWonPoints(Integer winnerWonPoints) {
		this.winnerWonPoints = winnerWonPoints;
	}
	@Column(name="loser_lost_points", nullable = false, updatable = false, unique = false)
	public Integer getLoserLostPoints() {
		return loserLostPoints;
	}
	public void setLoserLostPoints(Integer loserLostPoints) {
		this.loserLostPoints = loserLostPoints;
	}
}
