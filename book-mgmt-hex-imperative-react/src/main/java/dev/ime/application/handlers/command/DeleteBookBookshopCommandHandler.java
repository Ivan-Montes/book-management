package dev.ime.application.handlers.command;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.usecases.command.DeleteBookBookshopCommand;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.common.mapper.EventMapper;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.model.BookBookshop;
import dev.ime.domain.model.Event;
import dev.ime.domain.port.outbound.CompositeIdReadRepositoryPort;
import dev.ime.domain.port.outbound.EventWriteRepositoryPort;
import dev.ime.infrastructure.entity.BookBookshopId;

@Component
public class DeleteBookBookshopCommandHandler implements CommandHandler {

	private final EventWriteRepositoryPort eventWriteRepository;
	private final EventMapper eventMapper;
	private final CompositeIdReadRepositoryPort<BookBookshop> bookBookshopReadRepositoryAdapter;
	
	public DeleteBookBookshopCommandHandler(EventWriteRepositoryPort eventWriteRepository, EventMapper eventMapper,
			CompositeIdReadRepositoryPort<BookBookshop> bookBookshopReadRepositoryAdapter) {
		super();
		this.eventWriteRepository = eventWriteRepository;
		this.eventMapper = eventMapper;
		this.bookBookshopReadRepositoryAdapter = bookBookshopReadRepositoryAdapter;
	}

	@Override
	public Optional<Event> handle(Command command) {

		if (command instanceof DeleteBookBookshopCommand deleteCommand) {

			validateDelete(deleteCommand);
			
			Event event = eventMapper.createEvent(GlobalConstants.BBS_CAT, GlobalConstants.BBS_DELETED, deleteCommand);
			return eventWriteRepository.save(event);
			
		} else {
			throw new IllegalArgumentException(command.getClass().toString());
		}	
	}

	private void validateDelete(DeleteBookBookshopCommand deleteCommand) {
		
		Long bookId = deleteCommand.bookId();
		Long bookshopId = deleteCommand.bookshopId();
		BookBookshopId bookBookshopId = new BookBookshopId(bookId, bookshopId);
		Optional<BookBookshop> opt = bookBookshopReadRepositoryAdapter.findById(bookId, bookshopId);
		checkExistsBookBookshop(bookBookshopId, opt);
	}
	
	private void checkExistsBookBookshop(BookBookshopId id, Optional<?> opt) {

		if (opt.isEmpty()) {
			throw new ResourceNotFoundException(Map.of(GlobalConstants.BBS_ID, String.valueOf(id)));
		}	
	}
}
