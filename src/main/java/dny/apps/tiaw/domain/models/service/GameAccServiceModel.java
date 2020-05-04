package dny.apps.tiaw.domain.models.service;

import java.util.List;
import java.util.Set;

public class GameAccServiceModel {
	private String username;
	private Set<DeckServiceModel> decks;
	private List<CardServiceModel> cards;
	private DeckServiceModel defenseDeck;
	private DeckServiceModel attackDeck;
	private Long gold;
	private Long diamonds;
	private Long battlePoints;
	
	public GameAccServiceModel() {}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Set<DeckServiceModel> getDecks() {
		return decks;
	}

	public void setDecks(Set<DeckServiceModel> decks) {
		this.decks = decks;
	}

	public List<CardServiceModel> getCards() {
		return cards;
	}

	public void setCards(List<CardServiceModel> cards) {
		this.cards = cards;
	}
	
	public DeckServiceModel getDefenseDeck() {
		return defenseDeck;
	}

	public void setDefenseDeck(DeckServiceModel defenseDeck) {
		this.defenseDeck = defenseDeck;
	}

	public Long getGold() {
		return gold;
	}

	public void setGold(Long gold) {
		this.gold = gold;
	}

	public Long getDiamonds() {
		return diamonds;
	}

	public void setDiamonds(Long diamonds) {
		this.diamonds = diamonds;
	}

	public Long getBattlePoints() {
		return battlePoints;
	}

	public void setBattlePoints(Long battlePoints) {
		this.battlePoints = battlePoints;
	}

	public DeckServiceModel getAttackDeck() {
		return attackDeck;
	}

	public void setAttackDeck(DeckServiceModel attackDeck) {
		this.attackDeck = attackDeck;
	}
}
