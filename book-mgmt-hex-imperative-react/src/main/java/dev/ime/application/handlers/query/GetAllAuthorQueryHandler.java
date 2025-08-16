package dev.ime.application.handlers.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import dev.ime.application.usecases.query.GetAllAuthorQuery;
import dev.ime.domain.model.Author;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;

@Component
public class GetAllAuthorQueryHandler implements QueryHandler <Page<Author>> {

	private final ReadRepositoryPort<Author> authorReadRepositoryAdapter;
	
	public GetAllAuthorQueryHandler(ReadRepositoryPort<Author> authorReadRepositoryAdapter) {
		super();
		this.authorReadRepositoryAdapter = authorReadRepositoryAdapter;
	}

	@Override
	public Page<Author> handle(Query query) {
		
		if (query instanceof GetAllAuthorQuery(Pageable pageable)) {
			
			return authorReadRepositoryAdapter.findAll(pageable);
			
		} else {
			throw new IllegalArgumentException(query.getClass().toString());
		}	
	}
}
