package dny.apps.tiaw.error.role;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason = "Role does not exist!")
public class RoleNotFoundException extends RuntimeException {
	public RoleNotFoundException(String message) {
		super(message);
	}
}
