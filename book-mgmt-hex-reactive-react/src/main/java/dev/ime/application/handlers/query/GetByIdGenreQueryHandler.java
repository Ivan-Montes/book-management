package dev.ime.application.handlers.query;

import org.springframework.stereotype.Component;

import dev.ime.application.usecases.query.GetByIdGenreQuery;
import dev.ime.domain.model.Genre;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;
import reactor.core.publisher.Mono;

@Component
public class GetByIdGenreQueryHandler implements QueryHandler <Mono<Genre>> {

	private final ReadRepositoryPort<Genre> genreReadRepositoryAdapter;
	
	public GetByIdGenreQueryHandler(ReadRepositoryPort<Genre> genreReadRepositoryAdapter) {
		super();
		this.genreReadRepositoryAdapter = genreReadRepositoryAdapter;
	}

	@Override
	public Mono<Genre> handle(Query query) {

		return Mono.justOrEmpty(query)
				.cast(GetByIdGenreQuery.class)
				.map(GetByIdGenreQuery::id)
				.flatMap(genreReadRepositoryAdapter::findById);
	}	
}
