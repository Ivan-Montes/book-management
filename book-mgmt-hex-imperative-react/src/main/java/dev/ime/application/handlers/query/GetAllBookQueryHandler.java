package dev.ime.application.handlers.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import dev.ime.application.usecases.query.GetAllBookQuery;
import dev.ime.domain.model.Book;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;

@Component
public class GetAllBookQueryHandler implements QueryHandler <Page<Book>> {

	private final ReadRepositoryPort<Book> bookReadRepositoryAdapter;
	
	public GetAllBookQueryHandler(ReadRepositoryPort<Book> bookReadRepositoryAdapter) {
		super();
		this.bookReadRepositoryAdapter = bookReadRepositoryAdapter;
	}

	@Override
	public Page<Book> handle(Query query) {

		if (query instanceof GetAllBookQuery(Pageable pageable)) {
			
			return bookReadRepositoryAdapter.findAll(pageable);
			
		} else {
			throw new IllegalArgumentException(query.getClass().toString());
		}	
	}
}
