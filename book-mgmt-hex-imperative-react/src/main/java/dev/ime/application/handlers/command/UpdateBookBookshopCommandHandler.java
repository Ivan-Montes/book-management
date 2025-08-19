package dev.ime.application.handlers.command;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.usecases.command.UpdateBookBookshopCommand;
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
public class UpdateBookBookshopCommandHandler implements CommandHandler {

	private final EventWriteRepositoryPort eventWriteRepository;
	private final EventMapper eventMapper;
	private final CompositeIdReadRepositoryPort<BookBookshop> bookBookshopReadRepositoryAdapter;
	
	public UpdateBookBookshopCommandHandler(EventWriteRepositoryPort eventWriteRepository, EventMapper eventMapper,
			CompositeIdReadRepositoryPort<BookBookshop> bookBookshopReadRepositoryAdapter) {
		super();
		this.eventWriteRepository = eventWriteRepository;
		this.eventMapper = eventMapper;
		this.bookBookshopReadRepositoryAdapter = bookBookshopReadRepositoryAdapter;
	}

	@Override
	public Optional<Event> handle(Command command) {

		if (command instanceof UpdateBookBookshopCommand updateCommand) {

			validateUpdate(updateCommand);
			
			Event event = eventMapper.createEvent(GlobalConstants.BBS_CAT, GlobalConstants.BBS_UPDATED, updateCommand);
			return eventWriteRepository.save(event);
			
		} else {
			throw new IllegalArgumentException(command.getClass().toString());
		}	
	}

	private void validateUpdate(UpdateBookBookshopCommand updateCommand) {		

		Long bookId = updateCommand.bookId();
		Long bookshopId = updateCommand.bookshopId();
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
