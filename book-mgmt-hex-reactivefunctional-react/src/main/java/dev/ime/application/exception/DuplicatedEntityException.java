package dev.ime.application.exception;

import java.util.Map;
import java.util.UUID;

import dev.ime.common.constants.GlobalConstants;

public class DuplicatedEntityException extends BasicException {

	private static final long serialVersionUID = -8468920853099948654L;

	public DuplicatedEntityException(Map<String, String> errors) {
		super(
				UUID.randomUUID(),
				GlobalConstants.EX_DUP,
				GlobalConstants.EX_DUP_DESC,
				errors);
	}

}
