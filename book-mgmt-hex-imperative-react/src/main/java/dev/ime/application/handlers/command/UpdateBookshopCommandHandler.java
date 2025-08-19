package dev.ime.application.handlers.command;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.exception.UniqueValueException;
import dev.ime.application.usecases.command.UpdateBookshopCommand;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.common.mapper.EventMapper;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.model.Bookshop;
import dev.ime.domain.model.Event;
import dev.ime.domain.port.outbound.EventWriteRepositoryPort;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.domain.port.outbound.RequestByNameReadRepositoryPort;

@Component
public class UpdateBookshopCommandHandler implements CommandHandler {

	private final EventWriteRepositoryPort eventWriteRepository;
	private final EventMapper eventMapper;
	private final ReadRepositoryPort<Bookshop> bookshopReadRepositoryAdapter;
	private final RequestByNameReadRepositoryPort requestByNameReadRepositoryBookshop;	
	
	public UpdateBookshopCommandHandler(EventWriteRepositoryPort eventWriteRepository, EventMapper eventMapper,
			ReadRepositoryPort<Bookshop> bookshopReadRepositoryAdapter,
			@Qualifier("bookshopReadRepositoryAdapter")RequestByNameReadRepositoryPort requestByNameReadRepositoryBookshop) {
		super();
		this.eventWriteRepository = eventWriteRepository;
		this.eventMapper = eventMapper;
		this.bookshopReadRepositoryAdapter = bookshopReadRepositoryAdapter;
		this.requestByNameReadRepositoryBookshop = requestByNameReadRepositoryBookshop;
	}

	@Override
	public Optional<Event> handle(Command command) {

		if (command instanceof UpdateBookshopCommand updateCommand) {

			validateUpdate(updateCommand);
			
			Event event = eventMapper.createEvent(GlobalConstants.BS_CAT, GlobalConstants.BS_UPDATED, updateCommand);
			return eventWriteRepository.save(event);
			
		} else {
			throw new IllegalArgumentException(command.getClass().toString());
		}	
	}

	private void validateUpdate(UpdateBookshopCommand updateCommand) {
		
		Long bookshopId = updateCommand.bookshopId();
		Optional<Bookshop> opt = bookshopReadRepositoryAdapter.findById(bookshopId);
		
		checkExistsBookshop(bookshopId, opt);
		checkExistsByName(updateCommand.name(), bookshopId); 
	}

	private void checkExistsBookshop(Long bookshopId, Optional<?> opt) {

		if (opt.isEmpty()) {
			throw new ResourceNotFoundException(Map.of(GlobalConstants.BS_ID, String.valueOf(bookshopId)));
		}	
	}

	private void checkExistsByName(String name, Long bookshopId) {
		
		if (requestByNameReadRepositoryBookshop.findByNameAndIdNot(name, bookshopId)) {			
			throw new UniqueValueException(Map.of(GlobalConstants.BS_NAME, name));			
		}		
	}
}
