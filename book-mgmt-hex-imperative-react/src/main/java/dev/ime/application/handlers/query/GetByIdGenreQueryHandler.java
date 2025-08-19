package dev.ime.application.handlers.query;

import java.util.Optional;

import org.springframework.stereotype.Component;

import dev.ime.application.usecases.query.GetByIdGenreQuery;
import dev.ime.domain.model.Genre;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;

@Component
public class GetByIdGenreQueryHandler implements QueryHandler <Optional<Genre>> {

	private final ReadRepositoryPort<Genre> genreReadRepositoryAdapter;
	
	public GetByIdGenreQueryHandler(ReadRepositoryPort<Genre> genreReadRepositoryAdapter) {
		super();
		this.genreReadRepositoryAdapter = genreReadRepositoryAdapter;
	}

	@Override
	public Optional<Genre> handle(Query query) {

		if (query instanceof GetByIdGenreQuery(Long id)) {
			
			return genreReadRepositoryAdapter.findById(id);
			
		} else {
			throw new IllegalArgumentException(query.getClass().toString());
		}	
	}
}
