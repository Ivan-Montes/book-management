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
import dev.ime.application.dto.BookBookshopDto;
import dev.ime.application.exception.EmptyResponseException;
import dev.ime.application.exception.InvalidUUIDException;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.domain.port.inbound.CommandEndpointPort;
import dev.ime.domain.port.inbound.CompositeIdCommandServicePort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import reactor.core.publisher.Mono;

@Component
@Qualifier("bookBookshopCommandEndpointHandler")
public class BookBookshopCommandEndpointHandler implements CommandEndpointPort {

	private static final Logger logger = LoggerFactory.getLogger(BookBookshopCommandEndpointHandler.class);
	private final CompositeIdCommandServicePort<BookBookshopDto> bookBookshopService;
	private final DtoValidator dtoValidator;
	private final ApiExceptionHandler apiExceptionHandler;

	public BookBookshopCommandEndpointHandler(CompositeIdCommandServicePort<BookBookshopDto> bookBookshopService,
			DtoValidator dtoValidator, ApiExceptionHandler apiExceptionHandler) {
		super();
		this.bookBookshopService = bookBookshopService;
		this.dtoValidator = dtoValidator;
		this.apiExceptionHandler = apiExceptionHandler;
	}

	@Operation(
            summary = "Create bookbookshops",
            description = "Returns created bookbookshops",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "bookbookshops object that needs to be created",
                    required = true,
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = BookBookshopDto.class)
                    )
                )        
            )
    @ApiResponse(
        responseCode = "200", 
        description = "Created bookbookshops",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = BookBookshopDto.class)
        )
    )
	@Override
	public Mono<ServerResponse> create(ServerRequest serverRequest) {

		logger.info("POST /bookbookshops - Create new one");
		return serverRequest.bodyToMono(BookBookshopDto.class)
				.flatMap(dtoValidator::validateDto)
				.flatMap(bookBookshopService::create)
				.flatMap( objSaved -> ServerResponse.ok().bodyValue(objSaved))
				.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(
		                serverRequest.path(), GlobalConstants.MSG_NODATA
		            ))))				
				.onErrorResume(apiExceptionHandler::handleException);
	}

	@Operation(
            summary = "Update bookbookshops",
            description = "Returns updated bookbookshops",
            parameters = {
            		@Parameter(name = GlobalConstants.BOOK_ID, in = ParameterIn.PATH, required = true, description = "book ID", schema = @Schema(type = "string", format = "uuid")),
                    @Parameter(name = GlobalConstants.BS_ID, in = ParameterIn.PATH, required = true, description = "bookshop ID", schema = @Schema(type = "string", format = "uuid"))
                },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "bookbookshops object that needs to be updated",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BookBookshopDto.class)
                )
            )        
        )
    @ApiResponse(
        responseCode = "200", 
        description = "Updated bookbookshops",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = BookBookshopDto.class)
        		)
        )
	@Override
	public Mono<ServerResponse> update(ServerRequest serverRequest) {
		
		logger.info("PUT /bookbookshops/{} - Update", GlobalConstants.MODEL_ID);
		
		Mono<UUID> bookId = Mono.just(UUID.fromString(serverRequest.pathVariable(GlobalConstants.BOOK_ID)));
		Mono<UUID> bookshopId = Mono.just(UUID.fromString( serverRequest.pathVariable(GlobalConstants.BS_ID)));
		
		return Mono.zip(bookId, bookshopId)
				.flatMap( tuple -> serverRequest.bodyToMono(BookBookshopDto.class)
						.flatMap(dtoValidator::validateDto)
						.flatMap( dto -> bookBookshopService.update(tuple.getT1(), tuple.getT2(), dto))
						)
				.flatMap( objSaved -> ServerResponse.ok().bodyValue(objSaved))
				.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(
		                serverRequest.path(), GlobalConstants.MSG_NODATA
		            ))))					
				.onErrorResume(apiExceptionHandler::handleException);
	}

	@Operation(
            summary = "Delete bookbookshops",
            description = "Returns deleted bookbookshops",
            parameters = {
            		@Parameter(name = GlobalConstants.BOOK_ID, in = ParameterIn.PATH, required = true, description = "book ID", schema = @Schema(type = "string", format = "uuid")),
                    @Parameter(name = GlobalConstants.BS_ID, in = ParameterIn.PATH, required = true, description = "bookshop ID", schema = @Schema(type = "string", format = "uuid"))
                }
        )
    @ApiResponse(
        responseCode = "200", 
        description = "Deleted bookbookshops",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = BookBookshopDto.class)
        		)
        )
	@Override
	public Mono<ServerResponse> deleteById(ServerRequest serverRequest) {

		logger.info("DELETE /bookbookshops/{} - Delete", GlobalConstants.MODEL_ID);		

		return Mono.defer( () -> {
			
			try {
				
				UUID bookId = UUID.fromString( serverRequest.pathVariable(GlobalConstants.BOOK_ID));
				UUID bookshopId = UUID.fromString( serverRequest.pathVariable(GlobalConstants.BS_ID));
				
				return bookBookshopService
						.deleteById(bookId, bookshopId)
						.flatMap( obj -> ServerResponse.ok().bodyValue(obj))
						.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(
				                serverRequest.path(), GlobalConstants.MSG_NODATA
				            ))))				
						.onErrorResume(apiExceptionHandler::handleException);
				
			} catch (IllegalArgumentException error) {
					
	            return apiExceptionHandler.handleException(new InvalidUUIDException(Map.of(GlobalConstants.BBS_ID, GlobalConstants.MSG_NODATA)));	            
	        }			
		});
	}
}
