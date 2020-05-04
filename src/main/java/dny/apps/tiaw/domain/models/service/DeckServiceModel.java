package dny.apps.tiaw.domain.models.service;

import java.util.List;

public class DeckServiceModel extends BaseServiceModel {
	private List<CardServiceModel> cards;
	private String name;
	
	public DeckServiceModel() {}
	
	public List<CardServiceModel> getCards() {
		return cards;
	}
	public void setCards(List<CardServiceModel> cards) {
		this.cards = cards;
	}
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
