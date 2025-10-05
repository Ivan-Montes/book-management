package dev.ime.api.error;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;

import dev.ime.application.dto.ErrorResponse;
import dev.ime.application.exception.BasicException;
import dev.ime.application.exception.CreateEventException;
import dev.ime.application.exception.CreateJpaEntityException;
import dev.ime.application.exception.DuplicatedEntityException;
import dev.ime.application.exception.EmptyResponseException;
import dev.ime.application.exception.EntityAssociatedException;
import dev.ime.application.exception.EventUnexpectedException;
import dev.ime.application.exception.IllegalHandlerException;
import dev.ime.application.exception.InvalidUUIDException;
import dev.ime.application.exception.PublishEventException;
import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.exception.UniqueValueException;
import dev.ime.application.exception.ValidationException;
import dev.ime.common.constants.GlobalConstants;
import reactor.core.publisher.Mono;

@Component
public class ApiExceptionHandler {

	private final Map<Class<? extends Throwable>, Function<Throwable, Mono<ServerResponse>>> exceptionHandlers;
	private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

	public ApiExceptionHandler() {
		super();
		this.exceptionHandlers = initializeExceptionHandlers();
	}

	private Map<Class<? extends Throwable>, Function<Throwable, Mono<ServerResponse>>> initializeExceptionHandlers() {

		return Map.ofEntries(
				Map.entry(CreateEventException.class, this::handleBasicExceptionExtendedClasses),  
				Map.entry(CreateJpaEntityException.class, this::handleBasicExceptionExtendedClasses),  
				Map.entry(DuplicatedEntityException.class, this::handleBasicExceptionExtendedClasses),  
				Map.entry(EmptyResponseException.class, this::handleBasicExceptionExtendedClasses),  
				Map.entry(EntityAssociatedException.class, this::handleBasicExceptionExtendedClasses),  
				Map.entry(EventUnexpectedException.class, this::handleBasicExceptionExtendedClasses), 				 
				Map.entry(IllegalArgumentException.class, this::handleIllegalArgumentException),
				Map.entry(IllegalHandlerException.class, this::handleBasicExceptionExtendedClasses), 
				Map.entry(InvalidUUIDException.class, this::handleBasicExceptionExtendedClasses), 
				Map.entry(PublishEventException.class, this::handleBasicExceptionExtendedClasses),
				Map.entry(ResourceNotFoundException.class, this::handleBasicExceptionExtendedClasses),
				Map.entry(UniqueValueException.class, this::handleBasicExceptionExtendedClasses),
				Map.entry(ValidationException.class, this::handleBasicExceptionExtendedClasses)
				);
	}

	public Mono<ServerResponse> handleException(Throwable error) {

		return exceptionHandlers
				.entrySet()
				.stream()
				.filter(entry -> entry.getKey().isInstance(error))
				.findFirst()
				.map(entry -> entry.getValue().apply(error))
				.orElseGet(() -> handleGenericException(error));
	}

	private Mono<ServerResponse> handleGenericException(Throwable error) {

		return ServerResponse
				.status(HttpStatus.BAD_REQUEST)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(new ErrorResponse(UUID.randomUUID(), error.getClass().getSimpleName(),
						GlobalConstants.EX_PLAIN_DESC, Map.of(GlobalConstants.EX_PLAIN, error.getMessage())))
		.doOnNext(response -> logger.error(GlobalConstants.MSG_PATTERN_SEVERE, GlobalConstants.MSG_EVENT_ERROR, error.toString()));
	}

	public Mono<ServerResponse> handleIllegalArgumentException(Throwable error) {

		return ServerResponse
				.status(HttpStatus.UNPROCESSABLE_ENTITY)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(new ErrorResponse(UUID.randomUUID(), GlobalConstants.EX_ILLEGALARGUMENT,
						GlobalConstants.EX_ILLEGALARGUMENT_DESC, Map.of(GlobalConstants.EX_PLAIN, error.getMessage())))
				.doOnNext(response -> logger.error(GlobalConstants.MSG_PATTERN_SEVERE, GlobalConstants.MSG_EVENT_ERROR, error.toString()));
	}

	private Mono<ServerResponse> handleBasicExceptionExtendedClasses(Throwable error) {

		BasicException ex = (BasicException) error;

		return createServerResponse(ex);
	}

	private Mono<ServerResponse> createServerResponse(BasicException error) {

		return ServerResponse
				.status(HttpStatus.I_AM_A_TEAPOT)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(new ErrorResponse(error.getIdentifier(), error.getName(), error.getDescription(), error.getErrors()))
				.doOnNext(response -> logger.error(GlobalConstants.MSG_PATTERN_SEVERE, GlobalConstants.MSG_EVENT_ERROR, error.toString()));
	}
}
