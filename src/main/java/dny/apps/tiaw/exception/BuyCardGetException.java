package dny.apps.tiaw.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED, reason = "No buy card GET method")
public class BuyCardGetException extends RuntimeException {
	public BuyCardGetException(String message) {
		super(message);
	}
}
