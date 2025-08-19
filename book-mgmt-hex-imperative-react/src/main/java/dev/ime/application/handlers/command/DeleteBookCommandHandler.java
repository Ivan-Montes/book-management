package dev.ime.application.handlers.command;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import dev.ime.application.exception.EntityAssociatedException;
import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.usecases.command.DeleteBookCommand;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.common.mapper.EventMapper;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.model.Book;
import dev.ime.domain.model.Event;
import dev.ime.domain.port.outbound.EventWriteRepositoryPort;
import dev.ime.domain.port.outbound.ReadRepositoryPort;

@Component
public class DeleteBookCommandHandler implements CommandHandler {

	private final EventWriteRepositoryPort eventWriteRepository;
	private final EventMapper eventMapper;
	private final ReadRepositoryPort<Book> bookReadRepositoryAdapter;
	
	public DeleteBookCommandHandler(EventWriteRepositoryPort eventWriteRepository, EventMapper eventMapper,
			ReadRepositoryPort<Book> bookReadRepositoryAdapter) {
		super();
		this.eventWriteRepository = eventWriteRepository;
		this.eventMapper = eventMapper;
		this.bookReadRepositoryAdapter = bookReadRepositoryAdapter;
	}

	@Override
	public Optional<Event> handle(Command command) {

		if (command instanceof DeleteBookCommand deleteCommand) {

			validateDelete(deleteCommand);
			
			Event event = eventMapper.createEvent(GlobalConstants.BOOK_CAT, GlobalConstants.BOOK_DELETED, deleteCommand);
			return eventWriteRepository.save(event);
			
		} else {
			throw new IllegalArgumentException(command.getClass().toString());
		}	
	}

	private void validateDelete(DeleteBookCommand deleteCommand) {
		
		Long bookId = deleteCommand.bookId();
		Optional<Book> opt = bookReadRepositoryAdapter.findById(bookId);
		
		checkExistsBook(bookId, opt);
		checkAssociatedEntity(opt.get());
	}

	private void checkExistsBook(Long bookId, Optional<?> opt) {

		if (opt.isEmpty()) {
			throw new ResourceNotFoundException(Map.of(GlobalConstants.BOOK_ID, String.valueOf(bookId)));
		}	
	}

	private void checkAssociatedEntity(Book dom) {
				
		if (!dom.getBookshops().isEmpty()) {
			throw new EntityAssociatedException(Map.of(GlobalConstants.BOOK_ID, String.valueOf(dom.getBookId())));
		}
	}	
}
