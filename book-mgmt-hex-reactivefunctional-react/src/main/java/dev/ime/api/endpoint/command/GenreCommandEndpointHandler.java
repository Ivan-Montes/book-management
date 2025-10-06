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
import dev.ime.application.dto.GenreDto;
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
@Qualifier("genreCommandEndpointHandler")
public class GenreCommandEndpointHandler implements CommandEndpointPort {

	private static final Logger logger = LoggerFactory.getLogger(GenreCommandEndpointHandler.class);
	private final CommandServicePort<GenreDto> genreService;
	private final DtoValidator dtoValidator;
	private final ApiExceptionHandler apiExceptionHandler;
	
	public GenreCommandEndpointHandler(CommandServicePort<GenreDto> genreService, DtoValidator dtoValidator,
			ApiExceptionHandler apiExceptionHandler) {
		super();
		this.genreService = genreService;
		this.dtoValidator = dtoValidator;
		this.apiExceptionHandler = apiExceptionHandler;
	}

	@Operation(
            summary = "Create genres",
            description = "Returns created genres",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "genres object that needs to be created",
                    required = true,
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = GenreDto.class)
                    )
                )        
            )
    @ApiResponse(
        responseCode = "200", 
        description = "Created genres",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = GenreDto.class)
        )
    )
	@Override
	public Mono<ServerResponse> create(ServerRequest serverRequest) {
		
		logger.info("POST /genres - Create new one");
		return serverRequest.bodyToMono(GenreDto.class)
				.flatMap(dtoValidator::validateDto)
				.flatMap(genreService::create)
				.flatMap( objSaved -> ServerResponse.ok().bodyValue(objSaved))
				.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(
		                serverRequest.path(), GlobalConstants.MSG_NODATA
		            ))))				
				.onErrorResume(apiExceptionHandler::handleException);
	}

	@Operation(
            summary = "Update genres",
            description = "Returns updated genres",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "genres ID", schema = @Schema(type = "string", format = "uuid"))
                },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "genres object that needs to be updated",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = GenreDto.class)
                )
            )        
        )
    @ApiResponse(
        responseCode = "200", 
        description = "Updated genres",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = GenreDto.class)
        		)
        )
	@Override
	public Mono<ServerResponse> update(ServerRequest serverRequest) {
		
		logger.info("PUT /genres/{} - Update", GlobalConstants.MODEL_ID);
		return Mono.justOrEmpty(serverRequest.pathVariable(GlobalConstants.MODEL_ID))
				.map(UUID::fromString)
				.onErrorMap(IllegalArgumentException.class, error -> new InvalidUUIDException(Map.of(GlobalConstants.GENRE_ID, GlobalConstants.MSG_NODATA)))
				.flatMap( id -> serverRequest.bodyToMono(GenreDto.class)
						.flatMap(dtoValidator::validateDto)
						.flatMap( dto -> genreService.update(id, dto))
						)
				.flatMap( objSaved -> ServerResponse.ok().bodyValue(objSaved))
				.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(
		                serverRequest.path(), GlobalConstants.MSG_NODATA
		            ))))					
				.onErrorResume(apiExceptionHandler::handleException);
	}

	@Operation(
            summary = "Delete genres",
            description = "Returns deleted genres",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "genres ID", schema = @Schema(type = "string", format = "uuid"))
                }
        )
    @ApiResponse(
        responseCode = "200", 
        description = "Deleted genres",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = GenreDto.class)
        		)
        )
	@Override
	public Mono<ServerResponse> deleteById(ServerRequest serverRequest) {

		logger.info("DELETE /genres/{} - Delete", GlobalConstants.MODEL_ID);		
		return Mono.justOrEmpty(serverRequest.pathVariable(GlobalConstants.MODEL_ID))
				.map(UUID::fromString)
				.flatMap(genreService::deleteById)
				.flatMap( obj -> ServerResponse.ok().bodyValue(obj))
				.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(
		                serverRequest.path(), GlobalConstants.MSG_NODATA
		            ))))					
				.onErrorResume(apiExceptionHandler::handleException);
	}
}
