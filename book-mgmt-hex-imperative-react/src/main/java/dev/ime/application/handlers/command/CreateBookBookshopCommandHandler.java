package dev.ime.application.handlers.command;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.usecases.command.CreateBookBookshopCommand;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.common.mapper.EventMapper;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.model.Book;
import dev.ime.domain.model.Bookshop;
import dev.ime.domain.model.Event;
import dev.ime.domain.port.outbound.EventWriteRepositoryPort;
import dev.ime.domain.port.outbound.ReadRepositoryPort;

@Component
public class CreateBookBookshopCommandHandler implements CommandHandler {

	private final EventWriteRepositoryPort eventWriteRepository;
	private final EventMapper eventMapper;
	private final ReadRepositoryPort<Book> bookReadRepositoryAdapter;
	private final ReadRepositoryPort<Bookshop> bookshopReadRepositoryAdapter;
	
	public CreateBookBookshopCommandHandler(EventWriteRepositoryPort eventWriteRepository, EventMapper eventMapper,
			ReadRepositoryPort<Book> bookReadRepositoryAdapter,
			ReadRepositoryPort<Bookshop> bookshopReadRepositoryAdapter) {
		super();
		this.eventWriteRepository = eventWriteRepository;
		this.eventMapper = eventMapper;
		this.bookReadRepositoryAdapter = bookReadRepositoryAdapter;
		this.bookshopReadRepositoryAdapter = bookshopReadRepositoryAdapter;
	}

	@Override
	public Optional<Event> handle(Command command) {

		if (command instanceof CreateBookBookshopCommand createCommand) {
			
			validateCreate(createCommand);
			
			Event event = eventMapper.createEvent(GlobalConstants.BBS_CAT, GlobalConstants.BBS_CREATED, createCommand);
			return eventWriteRepository.save(event);
			
		} else {
			throw new IllegalArgumentException(command.getClass().toString());
		}	
	}

	private void validateCreate(CreateBookBookshopCommand createCommand) {
		
		checkExistsBookById(createCommand.bookId());
		checkExistsBookshopById(createCommand.bookshopId());		
	}
	
	private void checkExistsBookById(Long bookId) {

		if (bookReadRepositoryAdapter.findById(bookId).isEmpty()) {			
			throw new ResourceNotFoundException(Map.of(GlobalConstants.BOOK_ID, String.valueOf(bookId)));			
		}	
	}

	private void checkExistsBookshopById(Long bookshopId) {

		if (bookshopReadRepositoryAdapter.findById(bookshopId).isEmpty()) {			
			throw new ResourceNotFoundException(Map.of(GlobalConstants.BS_ID, String.valueOf(bookshopId)));			
		}	
	}
}
