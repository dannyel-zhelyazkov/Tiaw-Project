package dny.apps.tiaw.error.gameacc;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class GameAccOwnCardException extends RuntimeException {
	public GameAccOwnCardException(String message) {
		super(message);
	}
}
