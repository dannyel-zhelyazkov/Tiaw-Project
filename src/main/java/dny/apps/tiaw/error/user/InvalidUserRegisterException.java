package dny.apps.tiaw.error.user;

public class InvalidUserRegisterException extends RuntimeException {
	public InvalidUserRegisterException(String message) {
		super(message);
	}
}
