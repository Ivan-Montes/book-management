package dev.ime.api.endpoint.query;

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
import dev.ime.application.utils.PaginationUtils;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.domain.port.inbound.CompositeIdQueryServicePort;
import dev.ime.domain.port.inbound.QueryEndpointPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import reactor.core.publisher.Mono;

@Component
@Qualifier("bookBookshopQueryEndpointHandler")
public class BookBookshopQueryEndpointHandler implements QueryEndpointPort {

	private static final Logger logger = LoggerFactory.getLogger(BookBookshopQueryEndpointHandler.class);
	private final CompositeIdQueryServicePort<BookBookshopDto> bookBookshopService;
    private final PaginationUtils paginationUtils;
	private final DtoValidator dtoValidator;
	private final ApiExceptionHandler apiExceptionHandler;

	public BookBookshopQueryEndpointHandler(CompositeIdQueryServicePort<BookBookshopDto> bookBookshopService,
			PaginationUtils paginationUtils, DtoValidator dtoValidator, ApiExceptionHandler apiExceptionHandler) {
		super();
		this.bookBookshopService = bookBookshopService;
		this.paginationUtils = paginationUtils;
		this.dtoValidator = dtoValidator;
		this.apiExceptionHandler = apiExceptionHandler;
	}

	@Operation(
            summary = "Get all bookbookshops",
            description = "Returns a list of all available bookbookshops"
        )
    @ApiResponse(
        responseCode = "200", 
        description = "List of bookbookshops found",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = BookBookshopDto.class)
        )
    )
	@Override
	public Mono<ServerResponse> getAll(ServerRequest serverRequest) {
		
		logger.info("GET /bookbookshops (paginated) - Retrieve with pagination");

		return paginationUtils.createPaginationDto(serverRequest, BookBookshopDto.class)
				.flatMap(dtoValidator::validateDto)
		    	.map(paginationUtils::createPageRequest)
		    	.flatMap(bookBookshopService::getAll)
				.map(paginationUtils::createPaginatedResponse)
		        .flatMap(ServerResponse.ok()::bodyValue)
		        .switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(
		            serverRequest.path(), GlobalConstants.MSG_NODATA
		        ))))	 				
			    .onErrorResume(apiExceptionHandler::handleException);
	}

    @Operation(
            summary = "Get bookbookshops by ID",
            description = "Returns a specific bookbookshops based on its ID",
            parameters = {
                    @Parameter(name = GlobalConstants.BOOK_ID, in = ParameterIn.PATH, required = true, description = "book ID", schema = @Schema(type = "string", format = "uuid")),
                    @Parameter(name = GlobalConstants.BS_ID, in = ParameterIn.PATH, required = true, description = "bookshop ID", schema = @Schema(type = "string", format = "uuid"))
                    }
    		)
    @ApiResponse(
        responseCode = "200", 
        description = "bookbookshops found",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = BookBookshopDto.class)
        )
    )
	@Override
	public Mono<ServerResponse> getById(ServerRequest serverRequest) {
		
    	logger.info("GET /bookbookshops/{} - Retrieve by ID", GlobalConstants.MODEL_ID);

		return Mono.defer( () -> {
			
			try {
				
				UUID bookId = UUID.fromString( serverRequest.pathVariable(GlobalConstants.BOOK_ID));
				UUID bookshopId = UUID.fromString( serverRequest.pathVariable(GlobalConstants.BS_ID));
				
				return bookBookshopService
						.getById(bookId, bookshopId)
						.flatMap( dto -> ServerResponse.ok().bodyValue(dto))
						.switchIfEmpty(ServerResponse.notFound().build())			
						.onErrorResume(apiExceptionHandler::handleException);
				
			} catch (IllegalArgumentException error) {
					
	            return apiExceptionHandler.handleException(new InvalidUUIDException(Map.of(GlobalConstants.BBS_ID, GlobalConstants.MSG_NODATA)));	            
	        }			
		});
	}
}
