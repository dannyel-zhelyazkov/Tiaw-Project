package dny.apps.tiaw.error.card;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Card was not found")
public class CardNotFoundException extends RuntimeException {
	public CardNotFoundException(String message) {
        super(message);
    }
}
