package dev.ime.application.handlers.command;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.exception.UniqueValueException;
import dev.ime.application.usecases.command.UpdatePublisherCommand;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.common.mapper.EventMapper;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.model.Event;
import dev.ime.domain.model.Publisher;
import dev.ime.domain.port.outbound.EventWriteRepositoryPort;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.domain.port.outbound.RequestByNameReadRepositoryPort;

@Component
public class UpdatePublisherCommandHandler implements CommandHandler {

	private final EventWriteRepositoryPort eventWriteRepository;
	private final EventMapper eventMapper;
	private final ReadRepositoryPort<Publisher> publisherReadRepositoryAdapter;
	private final RequestByNameReadRepositoryPort requestByNameReadRepositoryPublisher;	
	
	public UpdatePublisherCommandHandler(EventWriteRepositoryPort eventWriteRepository, EventMapper eventMapper,
			ReadRepositoryPort<Publisher> publisherReadRepositoryAdapter,
			@Qualifier("publisherReadRepositoryAdapter")RequestByNameReadRepositoryPort requestByNameReadRepositoryPublisher) {
		super();
		this.eventWriteRepository = eventWriteRepository;
		this.eventMapper = eventMapper;
		this.publisherReadRepositoryAdapter = publisherReadRepositoryAdapter;
		this.requestByNameReadRepositoryPublisher = requestByNameReadRepositoryPublisher;
	}

	@Override
	public Optional<Event> handle(Command command) {

		if (command instanceof UpdatePublisherCommand updateCommand) {

			validateUpdate(updateCommand);
			
			Event event = eventMapper.createEvent(GlobalConstants.PUBLI_CAT, GlobalConstants.PUBLI_UPDATED, updateCommand);
			return eventWriteRepository.save(event);
			
		} else {
			throw new IllegalArgumentException(command.getClass().toString());
		}	
	}

	private void validateUpdate(UpdatePublisherCommand updateCommand) {
		
		Long publisherId = updateCommand.publisherId();
		Optional<Publisher> opt = publisherReadRepositoryAdapter.findById(publisherId);
		
		checkExistsPublisher(publisherId, opt);
		checkExistsByName(updateCommand.name(), publisherId); 
	}

	private void checkExistsPublisher(Long publisherId, Optional<?> opt) {

		if (opt.isEmpty()) {
			throw new ResourceNotFoundException(Map.of(GlobalConstants.PUBLI_ID, String.valueOf(publisherId)));
		}	
	}

	private void checkExistsByName(String name, Long publisherId) {
		
		if (requestByNameReadRepositoryPublisher.findByNameAndIdNot(name, publisherId)) {			
			throw new UniqueValueException(Map.of(GlobalConstants.PUBLI_NAME, name));			
		}		
	}
}
