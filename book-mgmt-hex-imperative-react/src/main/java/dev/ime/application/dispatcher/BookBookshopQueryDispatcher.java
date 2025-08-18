package dev.ime.application.dispatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import dev.ime.application.exception.IllegalHandlerException;
import dev.ime.application.handlers.query.GetAllBookBookshopQueryHandler;
import dev.ime.application.handlers.query.GetByIdBookBookshopQueryHandler;
import dev.ime.application.usecases.query.GetAllBookBookshopQuery;
import dev.ime.application.usecases.query.GetByIdBookBookshopQuery;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.domain.port.inbound.QueryDispatcher;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;

@Component
@Qualifier("bookBookshopQueryDispatcher")
public class BookBookshopQueryDispatcher implements QueryDispatcher {

	private final Map<Class<? extends Query>, QueryHandler<?>> queryHandlers = new HashMap<>();
	
	public BookBookshopQueryDispatcher(GetAllBookBookshopQueryHandler getAllQueryHandler, GetByIdBookBookshopQueryHandler getByIdQueryHandler) {
		super();
		queryHandlers.put(GetAllBookBookshopQuery.class, getAllQueryHandler);
		queryHandlers.put(GetByIdBookBookshopQuery.class, getByIdQueryHandler);
	}

	@Override
	public <T> QueryHandler<T> getQueryHandler(Query query) {

		@SuppressWarnings("unchecked")
		Optional<QueryHandler<T>> optHandler = Optional.ofNullable((QueryHandler<T>)queryHandlers.get(query.getClass()));
		
		return optHandler.orElseThrow( () -> new IllegalHandlerException(Map.of(GlobalConstants.OBJ_VALUE, query.getClass().getName())));	
	}
}
