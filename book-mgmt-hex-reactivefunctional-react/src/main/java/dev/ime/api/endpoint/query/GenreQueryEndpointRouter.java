package dev.ime.api.endpoint.query;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import dev.ime.common.config.UriConfigProperties;
import dev.ime.domain.port.inbound.QueryEndpointPort;

@Configuration
public class GenreQueryEndpointRouter {

	private final UriConfigProperties uriConfigProperties;
	private final QueryEndpointPort queryEndpointPort;
	
	public GenreQueryEndpointRouter(UriConfigProperties uriConfigProperties, 
			@Qualifier("genreQueryEndpointHandler")QueryEndpointPort queryEndpointPort) {
		super();
		this.uriConfigProperties = uriConfigProperties;
		this.queryEndpointPort = queryEndpointPort;
	}

	@RouterOperations({
		@RouterOperation(
				path = "/api/v1/genres", 
				beanClass = GenreQueryEndpointHandler.class, 
				beanMethod = "getAll",
		        method = RequestMethod.GET,
		        produces = MediaType.APPLICATION_JSON_VALUE),
	    @RouterOperation(
	    		path = "/api/v1/genres/{id}", 
	    		beanClass = GenreQueryEndpointHandler.class, 
	    		beanMethod = "getById",
	            method = RequestMethod.GET,
	            produces = MediaType.APPLICATION_JSON_VALUE
	    		)
		})
	@Bean
	RouterFunction<ServerResponse> genreQueryEndpointRoutes(){
		
		return RouterFunctions.nest(RequestPredicates.path(uriConfigProperties.getGenre()),
				RouterFunctions.nest(RequestPredicates.accept(MediaType.APPLICATION_JSON),
				RouterFunctions.route(RequestPredicates.GET(""), queryEndpointPort::getAll)
							.andRoute(RequestPredicates.GET("/{id}"), queryEndpointPort::getById)
				));
	}	
}
