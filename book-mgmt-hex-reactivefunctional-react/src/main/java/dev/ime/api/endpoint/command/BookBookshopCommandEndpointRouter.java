package dev.ime.api.endpoint.command;

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
import dev.ime.domain.port.inbound.CommandEndpointPort;

@Configuration
public class BookBookshopCommandEndpointRouter {

	private final UriConfigProperties uriConfigProperties;
	private final CommandEndpointPort commandEndpointPort;
	
	public BookBookshopCommandEndpointRouter(UriConfigProperties uriConfigProperties,
			@Qualifier("bookBookshopCommandEndpointHandler")CommandEndpointPort commandEndpointPort) {
		super();
		this.uriConfigProperties = uriConfigProperties;
		this.commandEndpointPort = commandEndpointPort;
	}

	@RouterOperations({
		@RouterOperation(
				path = "/api/v1/bookbookshops", 
				beanClass = BookBookshopCommandEndpointHandler.class, 
				beanMethod = "create",
		        method = RequestMethod.POST,
		        produces = MediaType.APPLICATION_JSON_VALUE),
	    @RouterOperation(
	    		path = "/api/v1/bookbookshops/book/{bookId}/bookshop/{bookshopId}", 
	    		beanClass = BookBookshopCommandEndpointHandler.class, 
	    		beanMethod = "update",
	            method = RequestMethod.PUT,
	            produces = MediaType.APPLICATION_JSON_VALUE
	    		),
	    @RouterOperation(
	    		path = "/api/v1/bookbookshops//book/{bookId}/bookshop/{bookshopId}", 
	    		beanClass = BookBookshopCommandEndpointHandler.class, 
	    		beanMethod = "deleteById",
	            method = RequestMethod.DELETE,
	            produces = MediaType.APPLICATION_JSON_VALUE
	    		)
		})
	@Bean
	RouterFunction<ServerResponse> bookBookshopCommandEndpointRoutes() {
		
		return RouterFunctions.nest(RequestPredicates.path(uriConfigProperties.getBookbookshop()),
			   RouterFunctions.nest(RequestPredicates.accept(MediaType.APPLICATION_JSON),
			   RouterFunctions.route(RequestPredicates.POST(""), commandEndpointPort::create)
   						   .andRoute(RequestPredicates.PUT("/book/{bookId}/bookshop/{bookshopId}"), commandEndpointPort::update)
   						   .andRoute(RequestPredicates.DELETE("/book/{bookId}/bookshop/{bookshopId}"), commandEndpointPort::deleteById)   						   
					   ));
	}
}
