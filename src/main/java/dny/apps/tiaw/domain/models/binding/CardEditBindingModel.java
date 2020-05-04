package dny.apps.tiaw.domain.models.binding;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

public class CardEditBindingModel {
	private String name;
	private Integer power;
	private Integer defense;
	
	public CardEditBindingModel() {}
	
	@NotNull
	@Size(min=3, max=20, message = "Name must be between 3 and 20 symbols long.")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@NotNull(message = "Power must be not empty")
	@PositiveOrZero(message = "Power must be 0 or positive.")
	public Integer getPower() {
		return power;
	}
	public void setPower(Integer power) {
		this.power = power;
	}
	@NotNull(message = "Defense must be not empty")
	@PositiveOrZero(message = "Defense must be 0 or positive.")
	public Integer getDefense() {
		return defense;
	}
	public void setDefense(Integer defense) {
		this.defense = defense;
	}
}
