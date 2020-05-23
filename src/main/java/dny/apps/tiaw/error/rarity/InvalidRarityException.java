package dny.apps.tiaw.error.rarity;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class InvalidRarityException extends RuntimeException {
	public InvalidRarityException(String message) {
		super(message);
	}
}
