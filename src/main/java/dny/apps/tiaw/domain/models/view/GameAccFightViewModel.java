package dny.apps.tiaw.domain.models.view;

public class GameAccFightViewModel {
	private String username;
	private DeckCardsViewModel defenseDeck;
	private DeckCardsViewModel attackDeck;
	private Long battlePoints;
	
	public GameAccFightViewModel() {}

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public DeckCardsViewModel getDefenseDeck() {
		return defenseDeck;
	}
	public void setDefenseDeck(DeckCardsViewModel defenseDeck) {
		this.defenseDeck = defenseDeck;
	}
	public DeckCardsViewModel getAttackDeck() {
		return attackDeck;
	}

	public void setAttackDeck(DeckCardsViewModel attackDeck) {
		this.attackDeck = attackDeck;
	}
	public Long getBattlePoints() {
		return battlePoints;
	}
	public void setBattlePoints(Long battlePoints) {
		this.battlePoints = battlePoints;
	}
}
