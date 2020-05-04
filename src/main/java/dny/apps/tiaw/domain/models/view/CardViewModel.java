package dny.apps.tiaw.domain.models.view;

public class CardViewModel {
	private String id;
	private String name;
	private Integer power;
	private Integer defense;
	private String rarity;
	private String url;
	private Integer price;
	
	public CardViewModel() {}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public String getRarity() {
		return rarity;
	}
	public void setRarity(String rarity) {
		this.rarity = rarity;
	}
	public void setDefense(Integer defense) {
		this.defense = defense;
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
}
