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
import dev.ime.application.dto.BookshopDto;
import dev.ime.application.exception.EmptyResponseException;
import dev.ime.application.exception.InvalidUUIDException;
import dev.ime.application.utils.PaginationUtils;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.domain.port.inbound.QueryEndpointPort;
import dev.ime.domain.port.inbound.QueryServicePort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import reactor.core.publisher.Mono;

@Component
@Qualifier("bookshopQueryEndpointHandler")
public class BookshopQueryEndpointHandler implements QueryEndpointPort {

	private static final Logger logger = LoggerFactory.getLogger(BookshopQueryEndpointHandler.class);
	private final QueryServicePort<BookshopDto> bookshopService;
    private final PaginationUtils paginationUtils;
	private final DtoValidator dtoValidator;
	private final ApiExceptionHandler apiExceptionHandler;
	
	public BookshopQueryEndpointHandler(QueryServicePort<BookshopDto> bookshopService, PaginationUtils paginationUtils,
			DtoValidator dtoValidator, ApiExceptionHandler apiExceptionHandler) {
		super();
		this.bookshopService = bookshopService;
		this.paginationUtils = paginationUtils;
		this.dtoValidator = dtoValidator;
		this.apiExceptionHandler = apiExceptionHandler;
	}

	@Operation(
            summary = "Get all bookshops",
            description = "Returns a list of all available bookshops"
        )
    @ApiResponse(
        responseCode = "200", 
        description = "List of bookshops found",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = BookshopDto.class)
        )
    )
	@Override
	public Mono<ServerResponse> getAll(ServerRequest serverRequest) {
		
		logger.info("GET /bookshops (paginated) - Retrieve with pagination");

		return paginationUtils.createPaginationDto(serverRequest, BookshopDto.class)
				.flatMap(dtoValidator::validateDto)
		    	.map(paginationUtils::createPageRequest)
		    	.flatMap(bookshopService::getAll)
				.map(paginationUtils::createPaginatedResponse)
		        .flatMap(ServerResponse.ok()::bodyValue)
		        .switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(
		            serverRequest.path(), GlobalConstants.MSG_NODATA
		        ))))	 				
			    .onErrorResume(apiExceptionHandler::handleException);
	}

    @Operation(
            summary = "Get bookshops by ID",
            description = "Returns a specific bookshops based on its ID",
            parameters = {
                    @Parameter(name = GlobalConstants.MODEL_ID, in = ParameterIn.PATH, required = true, description = "bookshop ID", schema = @Schema(type = "string", format = "uuid"))
                }
    		)
    @ApiResponse(
        responseCode = "200", 
        description = "bookshops found",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = BookshopDto.class)
        )
    )
	@Override
	public Mono<ServerResponse> getById(ServerRequest serverRequest) {
		
    	logger.info("GET /bookshops/{} - Retrieve by ID", GlobalConstants.MODEL_ID);

		return Mono.defer( () -> {
			
			try {
				
				UUID id = UUID.fromString( serverRequest.pathVariable(GlobalConstants.MODEL_ID));
				
				return bookshopService
						.getById(id)
						.flatMap( dto -> ServerResponse.ok().bodyValue(dto))
						.switchIfEmpty(ServerResponse.notFound().build())				
						.onErrorResume(apiExceptionHandler::handleException);
				
			} catch (IllegalArgumentException error) {
					
	            return apiExceptionHandler.handleException(new InvalidUUIDException(Map.of(GlobalConstants.BS_ID, GlobalConstants.MSG_NODATA)));	            
	        }			
		});
	}
}
