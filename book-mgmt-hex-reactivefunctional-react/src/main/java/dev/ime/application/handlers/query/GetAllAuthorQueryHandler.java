package dev.ime.application.handlers.query;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import dev.ime.application.usecases.query.GetAllAuthorQuery;
import dev.ime.domain.model.Author;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;
import reactor.core.publisher.Mono;

@Component
public class GetAllAuthorQueryHandler implements QueryHandler<Mono<Page<Author>>> {

	private final ReadRepositoryPort<Author> authorReadRepositoryAdapter;

	public GetAllAuthorQueryHandler(ReadRepositoryPort<Author> authorReadRepositoryAdapter) {
		super();
		this.authorReadRepositoryAdapter = authorReadRepositoryAdapter;
	}

	@Override
	public Mono<Page<Author>> handle(Query query) {
		return Mono.justOrEmpty(query)
				.cast(GetAllAuthorQuery.class)
				.map(GetAllAuthorQuery::pageable)
				.flatMap(authorReadRepositoryAdapter::findAll);
	}
}
