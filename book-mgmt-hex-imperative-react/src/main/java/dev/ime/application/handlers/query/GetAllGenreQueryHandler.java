package dev.ime.application.handlers.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import dev.ime.application.usecases.query.GetAllGenreQuery;
import dev.ime.domain.model.Genre;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;

@Component
public class GetAllGenreQueryHandler implements QueryHandler <Page<Genre>> {

	private final ReadRepositoryPort<Genre> genreReadRepositoryAdapter;
	
	public GetAllGenreQueryHandler(ReadRepositoryPort<Genre> genreReadRepositoryAdapter) {
		super();
		this.genreReadRepositoryAdapter = genreReadRepositoryAdapter;
	}

	@Override
	public Page<Genre> handle(Query query) {

		if (query instanceof GetAllGenreQuery(Pageable pageable)) {
			
			return genreReadRepositoryAdapter.findAll(pageable);
			
		} else {
			throw new IllegalArgumentException(query.getClass().toString());
		}	
	}
}
