package dny.apps.tiaw.error.rarity;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class RarityNotFoundException extends RuntimeException {
	public RarityNotFoundException(String message) {
		super(message);
	}
}
