package dev.ime.application.handlers.query;

import org.springframework.stereotype.Component;

import dev.ime.application.usecases.query.GetByIdBookQuery;
import dev.ime.domain.model.Book;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;
import reactor.core.publisher.Mono;

@Component
public class GetByIdBookQueryHandler implements QueryHandler <Mono<Book>> {

	private final ReadRepositoryPort<Book> bookReadRepositoryAdapter;
	
	public GetByIdBookQueryHandler(ReadRepositoryPort<Book> bookReadRepositoryAdapter) {
		super();
		this.bookReadRepositoryAdapter = bookReadRepositoryAdapter;
	}

	@Override
	public Mono<Book> handle(Query query) {

		return Mono.justOrEmpty(query)
				.cast(GetByIdBookQuery.class)
				.map(GetByIdBookQuery::id)
				.flatMap(bookReadRepositoryAdapter::findById);
	}	
}
