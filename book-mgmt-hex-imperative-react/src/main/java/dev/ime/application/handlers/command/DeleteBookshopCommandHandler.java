package dev.ime.application.handlers.command;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import dev.ime.application.exception.EntityAssociatedException;
import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.usecases.command.DeleteBookshopCommand;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.common.mapper.EventMapper;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.model.Bookshop;
import dev.ime.domain.model.Event;
import dev.ime.domain.port.outbound.EventWriteRepositoryPort;
import dev.ime.domain.port.outbound.ReadRepositoryPort;

@Component
public class DeleteBookshopCommandHandler implements CommandHandler {

	private final EventWriteRepositoryPort eventWriteRepository;
	private final EventMapper eventMapper;
	private final ReadRepositoryPort<Bookshop> bookshopReadRepositoryAdapter;
	
	public DeleteBookshopCommandHandler(EventWriteRepositoryPort eventWriteRepository, EventMapper eventMapper,
			ReadRepositoryPort<Bookshop> bookshopReadRepositoryAdapter) {
		super();
		this.eventWriteRepository = eventWriteRepository;
		this.eventMapper = eventMapper;
		this.bookshopReadRepositoryAdapter = bookshopReadRepositoryAdapter;
	}

	@Override
	public Optional<Event> handle(Command command) {

		if (command instanceof DeleteBookshopCommand deleteCommand) {

			validateDelete(deleteCommand);
			
			Event event = eventMapper.createEvent(GlobalConstants.BS_CAT, GlobalConstants.BS_DELETED, deleteCommand);
			return eventWriteRepository.save(event);
			
		} else {
			throw new IllegalArgumentException(command.getClass().toString());
		}	
	}

	private void validateDelete(DeleteBookshopCommand deleteCommand) {
		
		Long bookshopId = deleteCommand.bookshopId();
		Optional<Bookshop> opt = bookshopReadRepositoryAdapter.findById(bookshopId);
		
		checkExistsBookshop(bookshopId, opt);
		checkAssociatedEntity(opt.get());
	}
	
	private void checkExistsBookshop(Long bookshopId, Optional<?> opt) {

		if (opt.isEmpty()) {
			throw new ResourceNotFoundException(Map.of(GlobalConstants.BS_ID, String.valueOf(bookshopId)));
		}	
	}

	private void checkAssociatedEntity(Bookshop dom) {
				
		if (!dom.getBooks().isEmpty()) {
			throw new EntityAssociatedException(Map.of(GlobalConstants.BS_ID, String.valueOf(dom.getBookshopId())));
		}
	}
}
