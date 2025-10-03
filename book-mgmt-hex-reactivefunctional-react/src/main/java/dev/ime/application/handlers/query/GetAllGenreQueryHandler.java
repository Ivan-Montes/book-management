package dev.ime.application.handlers.query;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import dev.ime.application.usecases.query.GetAllGenreQuery;
import dev.ime.domain.model.Genre;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;
import reactor.core.publisher.Mono;

@Component
public class GetAllGenreQueryHandler implements QueryHandler<Mono<Page<Genre>>> {

	private final ReadRepositoryPort<Genre> genreReadRepositoryAdapter;
	
	public GetAllGenreQueryHandler(ReadRepositoryPort<Genre> genreReadRepositoryAdapter) {
		super();
		this.genreReadRepositoryAdapter = genreReadRepositoryAdapter;
	}

	@Override
	public Mono<Page<Genre>> handle(Query query) {
		return Mono.justOrEmpty(query)
				.cast(GetAllGenreQuery.class)
				.map(GetAllGenreQuery::pageable)
				.flatMap(genreReadRepositoryAdapter::findAll);
	}
}
