package dev.ime.application.handlers.query;

import java.util.Optional;

import org.springframework.stereotype.Component;

import dev.ime.application.usecases.query.GetByIdBookshopQuery;
import dev.ime.domain.model.Bookshop;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;

@Component
public class GetByIdBookshopQueryHandler implements QueryHandler <Optional<Bookshop>> {

	private final ReadRepositoryPort<Bookshop> bookshopReadRepositoryAdapter;
	
	public GetByIdBookshopQueryHandler(ReadRepositoryPort<Bookshop> bookshopReadRepositoryAdapter) {
		super();
		this.bookshopReadRepositoryAdapter = bookshopReadRepositoryAdapter;
	}

	@Override
	public Optional<Bookshop> handle(Query query) {

		if (query instanceof GetByIdBookshopQuery(Long id)) {
			
			return bookshopReadRepositoryAdapter.findById(id);
			
		} else {
			throw new IllegalArgumentException(query.getClass().toString());
		}	
	}
}
