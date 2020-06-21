package dny.apps.tiaw.error.gameacc;

public class NotEnoughGoldException extends RuntimeException {
	public NotEnoughGoldException(String message) {
		super(message);
	}
}
