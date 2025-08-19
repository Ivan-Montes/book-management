package dev.ime.application.handlers.query;

import java.util.Optional;

import org.springframework.stereotype.Component;

import dev.ime.application.usecases.query.GetByIdAuthorQuery;
import dev.ime.domain.model.Author;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;

@Component
public class GetByIdAuthorQueryHandler implements QueryHandler <Optional<Author>> {

	private final ReadRepositoryPort<Author> authorReadRepositoryAdapter;
	
	public GetByIdAuthorQueryHandler(ReadRepositoryPort<Author> authorReadRepositoryAdapter) {
		super();
		this.authorReadRepositoryAdapter = authorReadRepositoryAdapter;
	}

	@Override
	public Optional<Author> handle(Query query) {
		
		if (query instanceof GetByIdAuthorQuery(Long id)) {
			
			return authorReadRepositoryAdapter.findById(id);
			
		} else {
			throw new IllegalArgumentException(query.getClass().toString());
		}	
	}
}
