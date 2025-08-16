package dev.ime.application.handlers.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import dev.ime.application.usecases.query.GetAllBookshopQuery;
import dev.ime.domain.model.Bookshop;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;

@Component
public class GetAllBookshopQueryHandler implements QueryHandler <Page<Bookshop>> {

	private final ReadRepositoryPort<Bookshop> bookshopReadRepositoryAdapter;
	
	public GetAllBookshopQueryHandler(ReadRepositoryPort<Bookshop> bookshopReadRepositoryAdapter) {
		super();
		this.bookshopReadRepositoryAdapter = bookshopReadRepositoryAdapter;
	}

	@Override
	public Page<Bookshop> handle(Query query) {

		if (query instanceof GetAllBookshopQuery(Pageable pageable)) {
			
			return bookshopReadRepositoryAdapter.findAll(pageable);
			
		} else {
			throw new IllegalArgumentException(query.getClass().toString());
		}	
	}
}
