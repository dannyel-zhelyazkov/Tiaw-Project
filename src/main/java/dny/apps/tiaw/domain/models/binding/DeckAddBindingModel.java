package dny.apps.tiaw.domain.models.binding;

import javax.validation.constraints.*;

public class DeckAddBindingModel {
	private String name;
	
	public DeckAddBindingModel() {}
	
	@NotNull
	@Size(min = 3, max = 10, message = "Name must be 3 to 10 characters")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
