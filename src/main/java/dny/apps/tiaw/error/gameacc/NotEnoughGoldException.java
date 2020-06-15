package dny.apps.tiaw.error.gameacc;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class NotEnoughGoldException extends RuntimeException {
	public NotEnoughGoldException(String message) {
		super(message);
	}
}
