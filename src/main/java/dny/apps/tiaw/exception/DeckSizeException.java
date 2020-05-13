package dny.apps.tiaw.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class DeckSizeException extends RuntimeException {
	public DeckSizeException(String message) {
		super(message);
	}
}
