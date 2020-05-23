package dny.apps.tiaw.domain.entities;

import java.util.Set;

import javax.persistence.*;

@Entity
@Table(name = "decks")
public class Deck extends BaseEntity {
	private Set<Card> cards;
	private String name;
	
	public Deck() {}

	@ManyToMany(fetch = FetchType.LAZY ,cascade = CascadeType.ALL)
	public Set<Card> getCards() {
		return cards;
	}
	public void setCards(Set<Card> cards) {
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
