package dev.ime.application.handlers.query;

import org.springframework.stereotype.Component;

import dev.ime.application.usecases.query.GetByIdPublisherQuery;
import dev.ime.domain.model.Publisher;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;
import reactor.core.publisher.Mono;

@Component
public class GetByIdPublisherQueryHandler implements QueryHandler <Mono<Publisher>> {

	private final ReadRepositoryPort<Publisher> publisherReadRepositoryAdapter;
	
	public GetByIdPublisherQueryHandler(ReadRepositoryPort<Publisher> publisherReadRepositoryAdapter) {
		super();
		this.publisherReadRepositoryAdapter = publisherReadRepositoryAdapter;
	}

	@Override
	public Mono<Publisher> handle(Query query) {

		return Mono.justOrEmpty(query)
				.cast(GetByIdPublisherQuery.class)
				.map(GetByIdPublisherQuery::id)
				.flatMap(publisherReadRepositoryAdapter::findById);
	}	
}
