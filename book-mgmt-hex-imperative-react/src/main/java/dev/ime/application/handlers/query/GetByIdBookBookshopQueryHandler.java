package dev.ime.application.handlers.query;

import java.util.Optional;

import org.springframework.stereotype.Component;

import dev.ime.application.usecases.query.GetByIdBookBookshopQuery;
import dev.ime.domain.model.BookBookshop;
import dev.ime.domain.port.outbound.CompositeIdReadRepositoryPort;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;

@Component
public class GetByIdBookBookshopQueryHandler implements QueryHandler <Optional<BookBookshop>> {

	private final CompositeIdReadRepositoryPort<BookBookshop> bookBookshopReadRepositoryAdapter;
	
	public GetByIdBookBookshopQueryHandler(
			CompositeIdReadRepositoryPort<BookBookshop> bookBookshopReadRepositoryAdapter) {
		super();
		this.bookBookshopReadRepositoryAdapter = bookBookshopReadRepositoryAdapter;
	}

	@Override
	public Optional<BookBookshop> handle(Query query) {

		if (query instanceof GetByIdBookBookshopQuery(Long bookId, Long bookshopId)) {
			
			return bookBookshopReadRepositoryAdapter.findById(bookId, bookshopId);
			
		} else {
			throw new IllegalArgumentException(query.getClass().toString());
		}	
	}
}
