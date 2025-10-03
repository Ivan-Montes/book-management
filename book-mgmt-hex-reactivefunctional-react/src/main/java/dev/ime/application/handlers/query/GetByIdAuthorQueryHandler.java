package dev.ime.application.handlers.query;

import org.springframework.stereotype.Component;

import dev.ime.application.usecases.query.GetByIdAuthorQuery;
import dev.ime.domain.model.Author;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;
import reactor.core.publisher.Mono;

@Component
public class GetByIdAuthorQueryHandler implements QueryHandler <Mono<Author>> {

	private final ReadRepositoryPort<Author> authorReadRepositoryAdapter;
	
	public GetByIdAuthorQueryHandler(ReadRepositoryPort<Author> authorReadRepositoryAdapter) {
		super();
		this.authorReadRepositoryAdapter = authorReadRepositoryAdapter;
	}

	@Override
	public Mono<Author> handle(Query query) {

		return Mono.justOrEmpty(query)
				.cast(GetByIdAuthorQuery.class)
				.map(GetByIdAuthorQuery::id)
				.flatMap(authorReadRepositoryAdapter::findById);
	}	
}
