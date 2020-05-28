package dny.apps.tiaw.domain.entities;

import java.sql.Timestamp;
import javax.persistence.*;

@Entity
@Table(name = "fights")
public class Fight extends BaseEntity {
	private Timestamp datetime;
	private GameAcc p1;
	private GameAcc p2;
	
	public Fight() {}

	@Temporal(TemporalType.TIMESTAMP)
	public Timestamp getDatetime() {
		return datetime;
	}
	public void setDatetime(Timestamp datetime) {
		this.datetime = datetime;
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
