package dev.ime.application.handlers.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import dev.ime.application.usecases.query.GetAllBookBookshopQuery;
import dev.ime.domain.model.BookBookshop;
import dev.ime.domain.port.outbound.CompositeIdReadRepositoryPort;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;

@Component
public class GetAllBookBookshopQueryHandler implements QueryHandler <Page<BookBookshop>> {

	private final CompositeIdReadRepositoryPort<BookBookshop> bookBookshopReadRepositoryAdapter;

	public GetAllBookBookshopQueryHandler(
			CompositeIdReadRepositoryPort<BookBookshop> bookBookshopReadRepositoryAdapter) {
		super();
		this.bookBookshopReadRepositoryAdapter = bookBookshopReadRepositoryAdapter;
	}

	@Override
	public Page<BookBookshop> handle(Query query) {

		if (query instanceof GetAllBookBookshopQuery(Pageable pageable)) {
			
			return bookBookshopReadRepositoryAdapter.findAll(pageable);
			
		} else {
			throw new IllegalArgumentException(query.getClass().toString());
		}	
	}
}
