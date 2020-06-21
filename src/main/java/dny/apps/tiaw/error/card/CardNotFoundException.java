package dny.apps.tiaw.error.card;

public class CardNotFoundException extends RuntimeException {
	public CardNotFoundException(String message) {
        super(message);
    }
}
