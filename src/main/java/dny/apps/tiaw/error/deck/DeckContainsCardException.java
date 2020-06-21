package dny.apps.tiaw.error.deck;

public class DeckContainsCardException extends RuntimeException{
	public DeckContainsCardException(String message) {
		super(message);
	}
}
