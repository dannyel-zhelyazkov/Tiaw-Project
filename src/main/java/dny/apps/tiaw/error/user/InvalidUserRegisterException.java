package dny.apps.tiaw.error.user;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(reason = "Invalid user properties")
public class InvalidUserRegisterException extends RuntimeException {
	public InvalidUserRegisterException(String message) {
		super(message);
	}
}
