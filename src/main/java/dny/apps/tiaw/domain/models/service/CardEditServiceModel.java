package dny.apps.tiaw.domain.models.service;

public class CardEditServiceModel {
	private String name;
	private String oldName;
	private Integer power;
	private Integer defense;
	private Integer price;
	
	public CardEditServiceModel() {}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOldName() {
		return oldName;
	}
	public void setOldName(String oldName) {
		this.oldName = oldName;
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
	public Integer getPrice() {
		return price;
	}
	public void setPrice(Integer price) {
		this.price = price;
	}	
}
