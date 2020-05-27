package dny.apps.tiaw.domain.entities;

import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "decks")
public class Deck extends BaseEntity {
	private List<Card> cards;
	private String name;
	
	public Deck() {}

	@ManyToMany(fetch = FetchType.LAZY ,cascade = CascadeType.ALL)
	public List<Card> getCards() {
		return cards;
	}
	public void setCards(List<Card> cards) {
		this.cards = cards;
	}
	@Column(name="name", nullable = false, updatable = true, unique = false)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
