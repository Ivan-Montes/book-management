package dev.ime.application.handlers.query;

import org.springframework.stereotype.Component;

import dev.ime.application.usecases.query.GetByIdBookBookshopQuery;
import dev.ime.domain.model.BookBookshop;
import dev.ime.domain.port.outbound.CompositeIdReadRepositoryPort;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;
import reactor.core.publisher.Mono;

@Component
public class GetByIdBookBookshopQueryHandler implements QueryHandler <Mono<BookBookshop>> {

	private final CompositeIdReadRepositoryPort<BookBookshop> bookBookshopReadRepositoryAdapter;
	
	public GetByIdBookBookshopQueryHandler(
			CompositeIdReadRepositoryPort<BookBookshop> bookBookshopReadRepositoryAdapter) {
		super();
		this.bookBookshopReadRepositoryAdapter = bookBookshopReadRepositoryAdapter;
	}

	@Override
	public Mono<BookBookshop> handle(Query query) {

		return Mono.justOrEmpty(query)
				.cast(GetByIdBookBookshopQuery.class)
				.flatMap(getByIdQuery -> bookBookshopReadRepositoryAdapter.findById(getByIdQuery.bookId(), getByIdQuery.bookshopId()));
	}	
}
