package dny.apps.tiaw.error.card;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class InvalidCardCreateEditException extends RuntimeException {
	public InvalidCardCreateEditException(String message) {
		super(message);
	}
}
