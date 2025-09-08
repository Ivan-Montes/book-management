package dev.ime.application.handlers.command;

import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

import dev.ime.application.exception.CreateEventException;
import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.exception.UniqueValueException;
import dev.ime.application.usecases.command.UpdateBookCommand;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.common.mapper.EventMapper;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.model.Author;
import dev.ime.domain.model.Book;
import dev.ime.domain.model.Event;
import dev.ime.domain.model.Genre;
import dev.ime.domain.model.Publisher;
import dev.ime.domain.port.outbound.BookSpecificReadRepositoryPort;
import dev.ime.domain.port.outbound.EventWriteRepositoryPort;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class UpdateBookCommandHandler implements CommandHandler {

	private final EventWriteRepositoryPort eventWriteRepository;
	private final EventMapper eventMapper;
	private final ReadRepositoryPort<Book> bookReadRepositoryAdapter;
	private final BookSpecificReadRepositoryPort bookSpecificReadRepository;
	private final ReadRepositoryPort<Genre> genreReadRepositoryAdapter;
	private final ReadRepositoryPort<Publisher> publisherReadRepositoryAdapter;
	private final ReadRepositoryPort<Author> authorReadRepositoryAdapter;	
	
	public UpdateBookCommandHandler(EventWriteRepositoryPort eventWriteRepository, EventMapper eventMapper,
			ReadRepositoryPort<Book> bookReadRepositoryAdapter,
			BookSpecificReadRepositoryPort bookSpecificReadRepository,
			ReadRepositoryPort<Genre> genreReadRepositoryAdapter,
			ReadRepositoryPort<Publisher> publisherReadRepositoryAdapter,
			ReadRepositoryPort<Author> authorReadRepositoryAdapter) {
		super();
		this.eventWriteRepository = eventWriteRepository;
		this.eventMapper = eventMapper;
		this.bookReadRepositoryAdapter = bookReadRepositoryAdapter;
		this.bookSpecificReadRepository = bookSpecificReadRepository;
		this.genreReadRepositoryAdapter = genreReadRepositoryAdapter;
		this.publisherReadRepositoryAdapter = publisherReadRepositoryAdapter;
		this.authorReadRepositoryAdapter = authorReadRepositoryAdapter;
	}

	@Override
	public Mono<Event> handle(Command command) {

		return Mono.justOrEmpty(command)
				.ofType(UpdateBookCommand.class)
				.flatMap(this::validateUpdate)
				.map(this::createEvent)
				.flatMap(eventWriteRepository::save)
				.switchIfEmpty(Mono.error(new CreateEventException(Map.of(GlobalConstants.UPDATE_BOOK, ""))));	
	}

	private Mono<UpdateBookCommand> validateUpdate(UpdateBookCommand command) {
		
		return Mono.justOrEmpty(command)
				.flatMap(this::checkExistsBook)
				.flatMap(this::checkExistsByIsbnAndIdNot)
				.flatMap(this::checkExistsGenreById)
				.flatMap(this::checkExistsPublisherById)
				.flatMap(this::checkExistsAuthorSetById)
				.thenReturn(command);
	}

	private Mono<UpdateBookCommand> checkExistsBook(UpdateBookCommand command) {
	    return bookReadRepositoryAdapter.findById(command.bookId())
	    		.switchIfEmpty(Mono.error(new ResourceNotFoundException(Map.of(GlobalConstants.BOOK_ID, String.valueOf(command.bookId())))))
	    		.thenReturn(command);
	}

	private Mono<UpdateBookCommand> checkExistsByIsbnAndIdNot(UpdateBookCommand command) {
	    return bookSpecificReadRepository.findByIsbnAndIdNot(command.isbn(), command.bookId())
	    		.filter(b -> !b)
	    		.switchIfEmpty(Mono.error(new UniqueValueException(Map.of(GlobalConstants.BOOK_ISBN, String.valueOf(command.isbn())))))
	    		.thenReturn(command);
	}

	private Mono<UpdateBookCommand> checkExistsGenreById(UpdateBookCommand command) {
	    return genreReadRepositoryAdapter.findById(command.genreId())
	    		.switchIfEmpty(Mono.error(new ResourceNotFoundException(Map.of(GlobalConstants.GENRE_ID, String.valueOf(command.genreId())))))
	    		.thenReturn(command);
	}

	private Mono<UpdateBookCommand> checkExistsPublisherById(UpdateBookCommand command) {
	    return publisherReadRepositoryAdapter.findById(command.publisherId())
	    		.switchIfEmpty(Mono.error(new ResourceNotFoundException(Map.of(GlobalConstants.PUBLI_ID, String.valueOf(command.publisherId())))))
	    		.thenReturn(command);
	}

	private Mono<UpdateBookCommand> checkExistsAuthorSetById(UpdateBookCommand command) {
	    return Flux.fromIterable(command.authorIdSet())
	            .concatMap(this::checkExistsAuthorById)
	            .then()
	    		.thenReturn(command); 
	}
	
	private Mono<UUID> checkExistsAuthorById(UUID id) {
	    return authorReadRepositoryAdapter.findById(id)
	    		.switchIfEmpty(Mono.error(new ResourceNotFoundException(Map.of(GlobalConstants.AUTHOR_ID, String.valueOf(id)))))
	    		.thenReturn(id);
	}
	
	private Event createEvent(Command command) {		
		return eventMapper.createEvent(GlobalConstants.BOOK_CAT, GlobalConstants.BOOK_UPDATED, command);
	}
}
