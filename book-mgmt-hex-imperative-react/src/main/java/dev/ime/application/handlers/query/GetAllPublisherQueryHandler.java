package dev.ime.application.handlers.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import dev.ime.application.usecases.query.GetAllPublisherQuery;
import dev.ime.domain.model.Publisher;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;

@Component
public class GetAllPublisherQueryHandler implements QueryHandler <Page<Publisher>> {

	private final ReadRepositoryPort<Publisher> publisherReadRepositoryAdapter;
	
	public GetAllPublisherQueryHandler(ReadRepositoryPort<Publisher> publisherReadRepositoryAdapter) {
		super();
		this.publisherReadRepositoryAdapter = publisherReadRepositoryAdapter;
	}

	@Override
	public Page<Publisher> handle(Query query) {

		if (query instanceof GetAllPublisherQuery(Pageable pageable)) {
			
			return publisherReadRepositoryAdapter.findAll(pageable);
			
		} else {
			throw new IllegalArgumentException(query.getClass().toString());
		}	
	}
}
