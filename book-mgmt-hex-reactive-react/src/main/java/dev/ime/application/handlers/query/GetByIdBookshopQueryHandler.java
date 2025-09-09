package dev.ime.application.handlers.query;

import org.springframework.stereotype.Component;

import dev.ime.application.usecases.query.GetByIdBookshopQuery;
import dev.ime.domain.model.Bookshop;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;
import reactor.core.publisher.Mono;

@Component
public class GetByIdBookshopQueryHandler implements QueryHandler <Mono<Bookshop>> {

	private final ReadRepositoryPort<Bookshop> bookshopReadRepositoryAdapter;
	
	public GetByIdBookshopQueryHandler(ReadRepositoryPort<Bookshop> bookshopReadRepositoryAdapter) {
		super();
		this.bookshopReadRepositoryAdapter = bookshopReadRepositoryAdapter;
	}

	@Override
	public Mono<Bookshop> handle(Query query) {

		return Mono.justOrEmpty(query)
				.cast(GetByIdBookshopQuery.class)
				.map(GetByIdBookshopQuery::id)
				.flatMap(bookshopReadRepositoryAdapter::findById);
	}	
}
