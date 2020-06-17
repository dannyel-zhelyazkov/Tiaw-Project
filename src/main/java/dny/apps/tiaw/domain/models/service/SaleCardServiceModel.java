package dny.apps.tiaw.domain.models.service;

import java.time.LocalDateTime;

import dny.apps.tiaw.domain.entities.Rarity;

public class SaleCardServiceModel extends BaseServiceModel {
	private String name;
	private Integer power;
	private Integer defense;
	private Rarity rarity;
	private String url;
	private Integer price;
	private LocalDateTime releaseDate;
	
	public SaleCardServiceModel() {}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getPower() {
		return power;
	}

	public void setPower(Integer power) {
		this.power = power;
	}

	public Integer getDefense() {
		return defense;
	}

	public void setDefense(Integer defense) {
		this.defense = defense;
	}

	public Rarity getRarity() {
		return rarity;
	}

	public void setRarity(Rarity rarity) {
		this.rarity = rarity;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}
	
	public LocalDateTime getReleaseDate() {
		return releaseDate;
	}
	
	public void setReleaseDate(LocalDateTime releaseDate) {
		this.releaseDate = releaseDate;
	}
}
