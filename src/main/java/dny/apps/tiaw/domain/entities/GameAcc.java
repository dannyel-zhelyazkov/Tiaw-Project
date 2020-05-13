package dny.apps.tiaw.domain.entities;

import java.util.Set;

import javax.persistence.*;

@Entity
@Table(name="game_accs")
public class GameAcc extends BaseEntity {
	private Set<Deck> decks;
	private Set<Card> cards;
	private Deck defenseDeck;
	private Deck attackDeck;
	private Long gold;
	private Long battlePoints;
	private Integer attackTickets;
	
	public GameAcc() {}
	
	@ManyToMany(cascade = CascadeType.ALL)
    public Set<Deck> getDecks() {
		return decks;
	}   
    public void setDecks(Set<Deck> decks) {
		this.decks = decks;
	}  
    @ManyToMany(cascade = CascadeType.ALL)
    public Set<Card> getCards() {
		return cards;
	}   
    public void setCards(Set<Card> cards) {
		this.cards = cards;
	}    
    @OneToOne
    public Deck getDefenseDeck() {
		return defenseDeck;
	}
	public void setDefenseDeck(Deck defenseDeck) {
		this.defenseDeck = defenseDeck;
	}
	@OneToOne
    public Deck getAttackDeck() {
		return attackDeck;
	}
	public void setAttackDeck(Deck attackDeck) {
		this.attackDeck = attackDeck;
	}
	@Column(name = "gold", unique=false, updatable=true, nullable=false)
    public Long getGold() {
		return gold;
	}   
    public void setGold(Long gold) {
		this.gold = gold;
	}
    @Column(name = "battle_points", unique=false, updatable=true, nullable=false)
	public Long getBattlePoints() {
		return battlePoints;
	}
	public void setBattlePoints(Long battlePoints) {
		this.battlePoints = battlePoints;
	}
	@Column(name = "attack_tickets", unique=false, updatable=true, nullable=false)
	public Integer getAttackTickets() {
		return attackTickets;
	}
	public void setAttackTickets(Integer attackTickets) {
		this.attackTickets = attackTickets;
	}
}
