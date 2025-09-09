package dev.ime.application.handlers.query;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import dev.ime.application.usecases.query.GetAllBookBookshopQuery;
import dev.ime.domain.model.BookBookshop;
import dev.ime.domain.port.outbound.CompositeIdReadRepositoryPort;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;
import reactor.core.publisher.Mono;

@Component
public class GetAllBookBookshopQueryHandler implements QueryHandler<Mono<Page<BookBookshop>>> {

	private final CompositeIdReadRepositoryPort<BookBookshop> bookBookshopReadRepositoryAdapter;

	public GetAllBookBookshopQueryHandler(
			CompositeIdReadRepositoryPort<BookBookshop> bookBookshopReadRepositoryAdapter) {
		super();
		this.bookBookshopReadRepositoryAdapter = bookBookshopReadRepositoryAdapter;
	}

	@Override
	public Mono<Page<BookBookshop>> handle(Query query) {
		return Mono.justOrEmpty(query)
				.cast(GetAllBookBookshopQuery.class)
				.map(GetAllBookBookshopQuery::pageable)
				.flatMap(bookBookshopReadRepositoryAdapter::findAll);
	}
}
