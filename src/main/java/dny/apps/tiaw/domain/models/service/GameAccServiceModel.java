package dny.apps.tiaw.domain.models.service;

import java.util.Set;

public class GameAccServiceModel {
	private String username;
	private Set<DeckServiceModel> decks;
	private Set<CardServiceModel> cards;
	private DeckServiceModel defenseDeck;
	private DeckServiceModel attackDeck;
	private Long gold;
	private Long battlePoints;
	private Integer attackTickets;
	
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

	public Set<CardServiceModel> getCards() {
		return cards;
	}

	public void setCards(Set<CardServiceModel> cards) {
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

	public Integer getAttackTickets() {
		return attackTickets;
	}

	public void setAttackTickets(Integer attackTickets) {
		this.attackTickets = attackTickets;
	}
}
