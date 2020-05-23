package dny.apps.tiaw.error.deck;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class InvalidDeckCreateException extends RuntimeException {

	public InvalidDeckCreateException(String message) {
		super(message);
	}

}
