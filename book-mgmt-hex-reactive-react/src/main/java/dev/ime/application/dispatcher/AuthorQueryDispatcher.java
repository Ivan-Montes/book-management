package dev.ime.application.dispatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import dev.ime.application.exception.IllegalHandlerException;
import dev.ime.application.handlers.query.GetAllAuthorQueryHandler;
import dev.ime.application.handlers.query.GetByIdAuthorQueryHandler;
import dev.ime.application.usecases.query.GetAllAuthorQuery;
import dev.ime.application.usecases.query.GetByIdAuthorQuery;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.domain.port.inbound.QueryDispatcher;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;

@Component
@Qualifier("authorQueryDispatcher")
public class AuthorQueryDispatcher implements QueryDispatcher {

	private final Map<Class<? extends Query>, QueryHandler<?>> queryHandlers = new HashMap<>();
	
	public AuthorQueryDispatcher(GetAllAuthorQueryHandler getAllQueryHandler, GetByIdAuthorQueryHandler getByIdQueryHandler) {
		super();
		queryHandlers.put(GetAllAuthorQuery.class, getAllQueryHandler);
		queryHandlers.put(GetByIdAuthorQuery.class, getByIdQueryHandler);
	}

	@Override
	public <T> QueryHandler<T> getQueryHandler(Query query) {

		@SuppressWarnings("unchecked")
		Optional<QueryHandler<T>> optHandler = Optional.ofNullable((QueryHandler<T>)queryHandlers.get(query.getClass()));
		
		return optHandler.orElseThrow( () -> new IllegalHandlerException(Map.of(GlobalConstants.OBJ_VALUE, query.getClass().getName())));	
	}
}
