package dny.apps.tiaw.domain.models.service;

import java.util.Set;

public class DeckServiceModel extends BaseServiceModel {
	private Set<CardServiceModel> cards;
	private String name;
	
	public DeckServiceModel() {}
	
	public Set<CardServiceModel> getCards() {
		return cards;
	}
	public void setCards(Set<CardServiceModel> cards) {
		this.cards = cards;
	}
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
