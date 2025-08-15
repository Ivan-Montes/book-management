package dev.ime.application.exception;

import java.util.Map;
import java.util.UUID;

import dev.ime.common.constants.GlobalConstants;

public class EventUnexpectedException extends BasicException {

	private static final long serialVersionUID = -7876192023220281040L;

	public EventUnexpectedException(Map<String, String> errors) {
		super(
				UUID.randomUUID(), 
				GlobalConstants.EX_EVENTUNEXPEC, 
				GlobalConstants.EX_EVENTUNEXPEC_DESC, 
				errors);
		}
}
