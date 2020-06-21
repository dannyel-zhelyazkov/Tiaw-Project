package dny.apps.tiaw.error.deck;

public class DeckNotFoundException extends RuntimeException {
	public DeckNotFoundException(String message) {
        super(message);
    }
}