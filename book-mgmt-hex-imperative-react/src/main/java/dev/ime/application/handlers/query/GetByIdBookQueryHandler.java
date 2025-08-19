package dev.ime.application.handlers.query;

import java.util.Optional;

import org.springframework.stereotype.Component;

import dev.ime.application.usecases.query.GetByIdBookQuery;
import dev.ime.domain.model.Book;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;

@Component
public class GetByIdBookQueryHandler implements QueryHandler <Optional<Book>> {

	private final ReadRepositoryPort<Book> bookReadRepositoryAdapter;
	
	public GetByIdBookQueryHandler(ReadRepositoryPort<Book> bookReadRepositoryAdapter) {
		super();
		this.bookReadRepositoryAdapter = bookReadRepositoryAdapter;
	}

	@Override
	public Optional<Book> handle(Query query) {

		if (query instanceof GetByIdBookQuery(Long id)) {
			
			return bookReadRepositoryAdapter.findById(id);
			
		} else {
			throw new IllegalArgumentException(query.getClass().toString());
		}	
	}
}
