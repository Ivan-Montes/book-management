package dev.ime.application.handlers.query;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import dev.ime.application.usecases.query.GetAllPublisherQuery;
import dev.ime.domain.model.Publisher;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;
import reactor.core.publisher.Mono;

@Component
public class GetAllPublisherQueryHandler implements QueryHandler<Mono<Page<Publisher>>> {

	private final ReadRepositoryPort<Publisher> publisherReadRepositoryAdapter;
	
	public GetAllPublisherQueryHandler(ReadRepositoryPort<Publisher> publisherReadRepositoryAdapter) {
		super();
		this.publisherReadRepositoryAdapter = publisherReadRepositoryAdapter;
	}
	
	@Override
	public Mono<Page<Publisher>> handle(Query query) {
		return Mono.justOrEmpty(query)
				.cast(GetAllPublisherQuery.class)
				.map(GetAllPublisherQuery::pageable)
				.flatMap(publisherReadRepositoryAdapter::findAll);
	}
}
