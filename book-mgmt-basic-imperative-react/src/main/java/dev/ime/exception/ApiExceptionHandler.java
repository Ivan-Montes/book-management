package dev.ime.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import dev.ime.common.GlobalConstants;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);
	
	@ExceptionHandler(dev.ime.exception.ResourceNotFoundException.class)	
	public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex){

		final String msg = ex.toString();

		logger.error(GlobalConstants.MSG_PATTERN_SEVERE, GlobalConstants.EX_RESOURCENOTFOUND, msg);
		
		ErrorResponse response = createErrorResponse(ex);
		   
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}	

	@ExceptionHandler({
		dev.ime.exception.BasicException.class
		})	
	public ResponseEntity<ErrorResponse> handleBasicExceptionExtendedClasses(BasicException ex){

		final String msg = ex.toString();
		
		logger.error(GlobalConstants.MSG_PATTERN_SEVERE, GlobalConstants.EX_BASIC, msg);
		
		ErrorResponse response = createErrorResponse(ex);
		   
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	private ErrorResponse createErrorResponse(BasicException ex) {
		
		return new ErrorResponse(
				ex.getIdentifier(),
				ex.getName(),
				ex.getDescription(), 
				ex.getErrors()
    		);
	}	
}
