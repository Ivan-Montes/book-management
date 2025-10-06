package dev.ime.api.endpoint.command;

import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import dev.ime.api.error.ApiExceptionHandler;
import dev.ime.api.validation.DtoValidator;
import dev.ime.application.dto.BookDto;
import dev.ime.application.exception.EmptyResponseException;
import dev.ime.application.exception.InvalidUUIDException;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.domain.port.inbound.CommandEndpointPort;
import dev.ime.domain.port.inbound.CommandServicePort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import reactor.core.publisher.Mono;

@Component
@Qualifier("bookCommandEndpointHandler")
public class BookCommandEndpointHandler implements CommandEndpointPort {

	private static final Logger logger = LoggerFactory.getLogger(BookCommandEndpointHandler.class);
	private final CommandServicePort<BookDto> bookService;
	private final DtoValidator dtoValidator;
	private final ApiExceptionHandler apiExceptionHandler;
	
	public BookCommandEndpointHandler(CommandServicePort<BookDto> bookService, DtoValidator dtoValidator,
			ApiExceptionHandler apiExceptionHandler) {
		super();
		this.bookService = bookService;
		this.dtoValidator = dtoValidator;
		this.apiExceptionHandler = apiExceptionHandler;
	}

	@Operation(
            summary = "Create books",
            description = "Returns created books",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "books object that needs to be created",
                    required = true,
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = BookDto.class)
                    )
                )        
            )
    @ApiResponse(
        responseCode = "200", 
        description = "Created books",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = BookDto.class)
        )
    )
	@Override
	public Mono<ServerResponse> create(ServerRequest serverRequest) {
		
		logger.info("POST /books - Create new one");
		return serverRequest.bodyToMono(BookDto.class)
				.flatMap(dtoValidator::validateDto)
				.flatMap(bookService::create)
				.flatMap( objSaved -> ServerResponse.ok().bodyValue(objSaved))
				.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(
		                serverRequest.path(), GlobalConstants.MSG_NODATA
		            ))))				
				.onErrorResume(apiExceptionHandler::handleException);
	}

	@Operation(
            summary = "Update books",
            description = "Returns updated books",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "books ID", schema = @Schema(type = "string", format = "uuid"))
                },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "books object that needs to be updated",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BookDto.class)
                )
            )        
        )
    @ApiResponse(
        responseCode = "200", 
        description = "Updated books",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = BookDto.class)
        		)
        )
	@Override
	public Mono<ServerResponse> update(ServerRequest serverRequest) {
		
		logger.info("PUT /books/{} - Update", GlobalConstants.MODEL_ID);
		return Mono.justOrEmpty(serverRequest.pathVariable(GlobalConstants.MODEL_ID))
				.map(UUID::fromString)
				.onErrorMap(IllegalArgumentException.class, error -> new InvalidUUIDException(Map.of(GlobalConstants.BOOK_ID, GlobalConstants.MSG_NODATA)))
				.flatMap( id -> serverRequest.bodyToMono(BookDto.class)
						.flatMap(dtoValidator::validateDto)
						.flatMap( dto -> bookService.update(id, dto))
						)
				.flatMap( objSaved -> ServerResponse.ok().bodyValue(objSaved))
				.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(
		                serverRequest.path(), GlobalConstants.MSG_NODATA
		            ))))					
				.onErrorResume(apiExceptionHandler::handleException);
	}

	@Operation(
            summary = "Delete books",
            description = "Returns deleted books",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "books ID", schema = @Schema(type = "string", format = "uuid"))
                }
        )
    @ApiResponse(
        responseCode = "200", 
        description = "Deleted books",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = BookDto.class)
        		)
        )
	@Override
	public Mono<ServerResponse> deleteById(ServerRequest serverRequest) {
		
		logger.info("DELETE /books/{} - Delete", GlobalConstants.MODEL_ID);		
		return Mono.justOrEmpty(serverRequest.pathVariable(GlobalConstants.MODEL_ID))
				.map(UUID::fromString)
				.flatMap(bookService::deleteById)
				.flatMap( obj -> ServerResponse.ok().bodyValue(obj))
				.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(
		                serverRequest.path(), GlobalConstants.MSG_NODATA
		            ))))					
				.onErrorResume(apiExceptionHandler::handleException);
	}
}
