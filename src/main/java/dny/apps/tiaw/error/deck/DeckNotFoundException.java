package dny.apps.tiaw.error.deck;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Deck was not found")
public class DeckNotFoundException extends RuntimeException {
	public DeckNotFoundException(String message) {
        super(message);
    }
}