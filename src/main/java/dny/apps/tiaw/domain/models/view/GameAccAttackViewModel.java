package dny.apps.tiaw.domain.models.view;

public class GameAccAttackViewModel {
	private String username;
	private DeckCardsViewModel attackDeck;
	private Integer attackTickets;
	private Long battlePoints;
	
	public GameAccAttackViewModel() {}

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public DeckCardsViewModel getAttackDeck() {
		return attackDeck;
	}
	public void setAttackDeck(DeckCardsViewModel attackDeck) {
		this.attackDeck = attackDeck;
	}
	public Integer getAttackTickets() {
		return attackTickets;
	}
	public void setAttackTickets(Integer attackTickets) {
		this.attackTickets = attackTickets;
	}
	public Long getBattlePoints() {
		return battlePoints;
	}
	public void setBattlePoints(Long battlePoints) {
		this.battlePoints = battlePoints;
	}
}
