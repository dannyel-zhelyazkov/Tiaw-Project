package dny.apps.tiaw.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Deck already contains card")
public class DeckContainsCardException extends RuntimeException{
	public DeckContainsCardException(String message) {
		super(message);
	}
}
