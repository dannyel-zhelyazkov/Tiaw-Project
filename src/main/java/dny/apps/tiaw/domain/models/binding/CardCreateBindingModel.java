package dny.apps.tiaw.domain.models.binding;

import javax.validation.constraints.*;

import org.springframework.web.multipart.MultipartFile;

import dny.apps.tiaw.domain.entities.Rarity;

public class CardCreateBindingModel {
	private String name;
	private Integer power;
	private Integer defense;
	private Rarity rarity;
	private MultipartFile image = null;
	private boolean valid;

	public CardCreateBindingModel() {
	}

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
	public MultipartFile getImage() {
		return image;
	}
	public void setImage(MultipartFile image) {
		this.image = image;
	}
	@AssertTrue(message = "Image is required field")
	public boolean getValid() {
		valid = (image != null) && (!image.isEmpty());
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
}