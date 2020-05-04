package dny.apps.tiaw.domain.models.binding;

import javax.validation.constraints.*;

import org.springframework.web.multipart.MultipartFile;

import dny.apps.tiaw.domain.entities.Rarity;

public class CardAddBindingModel {
	private String name;
	private Integer power;
	private Integer defense;
	private Rarity rarity;
	private MultipartFile image = null;
	private Integer price;
	private boolean valid;

	public CardAddBindingModel() {
	}

	@NotNull
	@Size(min = 3, max = 20, message = "Name must be between 3 and 20 symbols long.")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@PositiveOrZero(message = "Power must be 0 or positive.")
	@NotNull(message = "Power must be not empty")
	public Integer getPower() {
		return power;
	}
	public void setPower(Integer power) {
		this.power = power;
	}
	@PositiveOrZero(message = "Defense must be 0 or positive.")
	@NotNull(message = "Defense must be not empty")
	public Integer getDefense() {
		return defense;
	}
	public void setDefense(Integer defense) {
		this.defense = defense;
	}
	@NotNull(message = "Rarity must be not empty")
	public Rarity getRarity() {
		return rarity;
	}
	public void setRarity(Rarity rarity) {
		this.rarity = rarity;
	}
	@NotNull(message = "Image url must be not empty.")
	public MultipartFile getImage() {
		return image;
	}
	public void setImage(MultipartFile image) {
		this.image = image;
	}
	@Positive(message = "Defense must be 0 or positive.")
	@NotNull(message = "Defense must be not empty")
	public Integer getPrice() {
		return price;
	}
	public void setPrice(Integer price) {
		this.price = price;
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