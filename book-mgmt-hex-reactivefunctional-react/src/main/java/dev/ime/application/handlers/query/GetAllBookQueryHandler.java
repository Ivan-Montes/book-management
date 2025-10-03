package dev.ime.application.handlers.query;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import dev.ime.application.usecases.query.GetAllBookQuery;
import dev.ime.domain.model.Book;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;
import reactor.core.publisher.Mono;

@Component
public class GetAllBookQueryHandler implements QueryHandler<Mono<Page<Book>>> {

	private final ReadRepositoryPort<Book> bookReadRepositoryAdapter;
	
	public GetAllBookQueryHandler(ReadRepositoryPort<Book> bookReadRepositoryAdapter) {
		super();
		this.bookReadRepositoryAdapter = bookReadRepositoryAdapter;
	}

	@Override
	public Mono<Page<Book>> handle(Query query) {
		return Mono.justOrEmpty(query)
				.cast(GetAllBookQuery.class)
				.map(GetAllBookQuery::pageable)
				.flatMap(bookReadRepositoryAdapter::findAll);
	}
}
