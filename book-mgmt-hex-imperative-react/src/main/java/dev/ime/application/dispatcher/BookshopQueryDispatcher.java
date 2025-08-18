package dev.ime.application.dispatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import dev.ime.application.exception.IllegalHandlerException;
import dev.ime.application.handlers.query.GetAllBookshopQueryHandler;
import dev.ime.application.handlers.query.GetByIdBookshopQueryHandler;
import dev.ime.application.usecases.query.GetAllBookshopQuery;
import dev.ime.application.usecases.query.GetByIdBookshopQuery;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.domain.port.inbound.QueryDispatcher;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;

@Component
@Qualifier("bookshopQueryDispatcher")
public class BookshopQueryDispatcher implements QueryDispatcher {

	private final Map<Class<? extends Query>, QueryHandler<?>> queryHandlers = new HashMap<>();
	
	public BookshopQueryDispatcher(GetAllBookshopQueryHandler getAllQueryHandler, GetByIdBookshopQueryHandler getByIdQueryHandler) {
		super();
		queryHandlers.put(GetAllBookshopQuery.class, getAllQueryHandler);
		queryHandlers.put(GetByIdBookshopQuery.class, getByIdQueryHandler);
	}

	@Override
	public <T> QueryHandler<T> getQueryHandler(Query query) {

		@SuppressWarnings("unchecked")
		Optional<QueryHandler<T>> optHandler = Optional.ofNullable((QueryHandler<T>)queryHandlers.get(query.getClass()));
		
		return optHandler.orElseThrow( () -> new IllegalHandlerException(Map.of(GlobalConstants.OBJ_VALUE, query.getClass().getName())));	
	}
}
