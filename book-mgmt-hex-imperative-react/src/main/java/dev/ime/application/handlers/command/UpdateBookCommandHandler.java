package dev.ime.application.handlers.command;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Component;

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
	public Optional<Event> handle(Command command) {

		if (command instanceof UpdateBookCommand updateCommand) {

			validateUpdate(updateCommand);
			
			Event event = eventMapper.createEvent(GlobalConstants.BOOK_CAT, GlobalConstants.BOOK_UPDATED, updateCommand);
			return eventWriteRepository.save(event);
			
		} else {
			throw new IllegalArgumentException(command.getClass().toString());
		}	
	}

	private void validateUpdate(UpdateBookCommand updateCommand) {
		
		Long bookId = updateCommand.bookId();
		Optional<Book> opt = bookReadRepositoryAdapter.findById(bookId);
		
		checkExistsBook(bookId, opt);
		checkExistsByIsbn(updateCommand.isbn(), bookId);
		checkExistsGenreById(updateCommand.genreId());
		checkExistsPublisherById(updateCommand.publisherId());
		checkExistsAuthorSetById(updateCommand.authorIdSet());
	}

	private void checkExistsBook(Long bookId, Optional<?> opt) {

		if (opt.isEmpty()) {
			throw new ResourceNotFoundException(Map.of(GlobalConstants.BOOK_ID, String.valueOf(bookId)));
		}	
	}

	private void checkExistsByIsbn(String isbn, Long bookId) {
		
		if (bookSpecificReadRepository.findByIsbnAndIdNot(isbn, bookId)) {			
			throw new UniqueValueException(Map.of(GlobalConstants.BOOK_ISBN, isbn));			
		}		
	}
	
	private void checkExistsGenreById(Long genreId) {
		
		if (genreReadRepositoryAdapter.findById(genreId).isEmpty()) {
			throw new ResourceNotFoundException(Map.of(GlobalConstants.GENRE_ID, String.valueOf(genreId)));
		}		
	}

	private void checkExistsPublisherById(Long publisherId) {

		if (publisherReadRepositoryAdapter.findById(publisherId).isEmpty()) {
			throw new ResourceNotFoundException(Map.of(GlobalConstants.PUBLI_ID, String.valueOf(publisherId)));
		}		
	}

	private void checkExistsAuthorSetById(Set<Long> authorIdSet) {
		
		if (!checkExistsAuthors(authorIdSet)) {
			throw new ResourceNotFoundException(Map.of(GlobalConstants.AUTHOR_ID, String.valueOf(authorIdSet)));
		}		
	}
	
	private boolean checkExistsAuthors(Set<Long> authorIdSet) {
		
		return authorIdSet.stream()
				.map(authorReadRepositoryAdapter::findById)
				.map(Optional::isPresent)
				.allMatch( bool -> bool.equals(true) );
	}
}
