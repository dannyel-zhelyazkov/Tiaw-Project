package dny.apps.tiaw.domain.models.binding;

public class CardDeleteBindingModel {
	private String name;
	private Integer power;
	private Integer defense;
	
	public CardDeleteBindingModel() {}
	
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
}
