package dny.apps.tiaw.error.deck;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class DeckSizeException extends RuntimeException {
	public DeckSizeException(String message) {
		super(message);
	}
}
