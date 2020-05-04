package dny.apps.tiaw.domain.entities;

import javax.persistence.*;

@Entity
@Table(name = "cards")
public class Card extends BaseEntity {
	private String name;
	private Integer power;
	private Integer defense;
	private Rarity rarity;
	private String url;
	private Integer price;

	public Card(){}

	@Column(name="name", nullable = false, unique = true, updatable = true)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Column(name="power", nullable = false, unique = false, updatable = true)
	public Integer getPower() {
		return power;
	}
	public void setPower(Integer power) {
		this.power = power;
	}
	@Column(name="defense", nullable = false, unique = false, updatable = true)
	public Integer getDefense() {
		return defense;
	}
	public void setDefense(Integer defense) {
		this.defense = defense;
	}
	@Enumerated(EnumType.STRING)
	public Rarity getRarity() {
		return rarity;
	}
	public void setRarity(Rarity rarity) {
		this.rarity = rarity;
	}
	@Column(name="url", nullable = false, updatable = true)
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	@Column(name="price", nullable = false, unique = false, updatable = true)
	public Integer getPrice() {
		return price;
	}
	public void setPrice(Integer price) {
		this.price = price;
	}
}
