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
	private GameAcc p1;
	private GameAcc p2;
	
	public Fight() {}

	@Column(name="fight_time", nullable = false, updatable = false, unique = false)
	public LocalDateTime getDatetime() {
		return fightTime;
	}
	public void setDatetime(LocalDateTime fightTime) {
		this.fightTime = fightTime;
	}
	@OneToOne
	public GameAcc getP1() {
		return p1;
	}
	public void setP1(GameAcc p1) {
		this.p1 = p1;
	}
	@OneToOne
	public GameAcc getP2() {
		return p2;
	}
	public void setP2(GameAcc p2) {
		this.p2 = p2;
	}
}
