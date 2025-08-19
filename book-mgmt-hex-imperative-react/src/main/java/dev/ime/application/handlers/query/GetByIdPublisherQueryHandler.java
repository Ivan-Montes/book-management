package dev.ime.application.handlers.query;

import java.util.Optional;

import org.springframework.stereotype.Component;

import dev.ime.application.usecases.query.GetByIdPublisherQuery;
import dev.ime.domain.model.Publisher;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;

@Component
public class GetByIdPublisherQueryHandler implements QueryHandler <Optional<Publisher>> {

	private final ReadRepositoryPort<Publisher> publisherReadRepositoryAdapter;
	
	public GetByIdPublisherQueryHandler(ReadRepositoryPort<Publisher> publisherReadRepositoryAdapter) {
		super();
		this.publisherReadRepositoryAdapter = publisherReadRepositoryAdapter;
	}

	@Override
	public Optional<Publisher> handle(Query query) {

		if (query instanceof GetByIdPublisherQuery(Long id)) {
			
			return publisherReadRepositoryAdapter.findById(id);
			
		} else {
			throw new IllegalArgumentException(query.getClass().toString());
		}	
	}
}
