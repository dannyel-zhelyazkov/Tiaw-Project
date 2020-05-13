package dny.apps.tiaw.domain.models.view;

import java.util.Set;

public class DeckCardsViewModel {
	private String id;
	private Set<CardViewModel> cards;
	private String name;
	
	public DeckCardsViewModel() {}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public Set<CardViewModel> getCards() {
		return cards;
	}

	public void setCards(Set<CardViewModel> cards) {
		this.cards = cards;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
