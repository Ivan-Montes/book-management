package dev.ime.application.handlers.query;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import dev.ime.application.usecases.query.GetAllBookshopQuery;
import dev.ime.domain.model.Bookshop;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;
import reactor.core.publisher.Mono;

@Component
public class GetAllBookshopQueryHandler implements QueryHandler<Mono<Page<Bookshop>>> {

	private final ReadRepositoryPort<Bookshop> bookshopReadRepositoryAdapter;
	
	public GetAllBookshopQueryHandler(ReadRepositoryPort<Bookshop> bookshopReadRepositoryAdapter) {
		super();
		this.bookshopReadRepositoryAdapter = bookshopReadRepositoryAdapter;
	}

	@Override
	public Mono<Page<Bookshop>> handle(Query query) {
		return Mono.justOrEmpty(query)
				.cast(GetAllBookshopQuery.class)
				.map(GetAllBookshopQuery::pageable)
				.flatMap(bookshopReadRepositoryAdapter::findAll);
	}
}
