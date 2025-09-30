package dev.ime.application.dispatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import dev.ime.application.exception.IllegalHandlerException;
import dev.ime.application.handlers.query.GetAllBookQueryHandler;
import dev.ime.application.handlers.query.GetByIdBookQueryHandler;
import dev.ime.application.usecases.query.GetAllBookQuery;
import dev.ime.application.usecases.query.GetByIdBookQuery;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.domain.port.inbound.QueryDispatcher;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;

@Component
@Qualifier("bookQueryDispatcher")
public class BookQueryDispatcher implements QueryDispatcher {

	private final Map<Class<? extends Query>, QueryHandler<?>> queryHandlers = new HashMap<>();
	
	public BookQueryDispatcher(GetAllBookQueryHandler getAllQueryHandler, GetByIdBookQueryHandler getByIdQueryHandler) {
		super();
		queryHandlers.put(GetAllBookQuery.class, getAllQueryHandler);
		queryHandlers.put(GetByIdBookQuery.class, getByIdQueryHandler);
	}

	@Override
	public <T> QueryHandler<T> getQueryHandler(Query query) {

		@SuppressWarnings("unchecked")
		Optional<QueryHandler<T>> optHandler = Optional.ofNullable((QueryHandler<T>)queryHandlers.get(query.getClass()));
		
		return optHandler.orElseThrow( () -> new IllegalHandlerException(Map.of(GlobalConstants.OBJ_VALUE, query.getClass().getName())));	
	}
}
