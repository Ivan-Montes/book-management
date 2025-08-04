package dev.ime.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import dev.ime.common.GlobalConstants;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
		
		final String msg = ex.getLocalizedMessage() != null? ex.getLocalizedMessage():GlobalConstants.MSG_NODATA;

		logger.error(GlobalConstants.MSG_PATTERN_SEVERE, GlobalConstants.EX_ARGNOVALID, msg);

		final Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach(error -> {
			final String fieldName = ((FieldError) error).getField();
			final String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});

		ErrorResponse response = createErrorResponse(errors);

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
		
		final String msg = ex.getLocalizedMessage() != null? ex.getLocalizedMessage():GlobalConstants.MSG_NODATA;

		logger.error(GlobalConstants.MSG_PATTERN_SEVERE, GlobalConstants.EX_ARGMISMATCH, msg);

		final String attrName = ex.getName();
		String typeName = "Unknown type";
		final Class<?> requiredType = ex.getRequiredType();

		if (requiredType != null && requiredType.getName() != null)
			typeName = requiredType.getName();

		ErrorResponse response = createErrorResponse(Map.of(attrName, typeName));

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(jakarta.validation.ConstraintViolationException.class)		
	public ResponseEntity<ErrorResponse> constraintViolationException(ConstraintViolationException ex){
		
		final String msg = ex.getLocalizedMessage() != null? ex.getLocalizedMessage():GlobalConstants.MSG_NODATA;

		logger.error(GlobalConstants.MSG_PATTERN_SEVERE, GlobalConstants.EX_CONSTRAINT, msg);
		
		ErrorResponse response = createErrorResponse(Map.of(GlobalConstants.EX_CONSTRAINT, msg));
		   
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse> dataIntegrityViolationException(DataIntegrityViolationException ex){
		
		final String msg = ex.getLocalizedMessage() != null? ex.getLocalizedMessage():GlobalConstants.MSG_NODATA;

		logger.error(GlobalConstants.MSG_PATTERN_SEVERE, GlobalConstants.EX_DATAINTEGRITY, msg);
		
		ErrorResponse response = createErrorResponse(Map.of(GlobalConstants.EX_DATAINTEGRITY, msg));
		   
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
		
		final String msg = ex.getLocalizedMessage() != null? ex.getLocalizedMessage():GlobalConstants.MSG_NODATA;

		logger.error(GlobalConstants.MSG_PATTERN_SEVERE, GlobalConstants.EX_PLAIN, msg);

	    ErrorResponse response = createErrorResponse(Map.of(GlobalConstants.EX_PLAIN, msg));

	    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private ErrorResponse createErrorResponse(Map<String, String> errors) {
		return new ErrorResponse(
	        UUID.randomUUID(),
	        GlobalConstants.EX_PLAIN,
	        GlobalConstants.EX_PLAIN_DESC,
	        errors
	    );
	}
}
