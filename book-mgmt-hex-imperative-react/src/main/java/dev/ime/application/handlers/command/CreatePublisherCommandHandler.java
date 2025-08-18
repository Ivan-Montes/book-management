package dev.ime.application.handlers.command;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import dev.ime.application.exception.UniqueValueException;
import dev.ime.application.usecases.command.CreatePublisherCommand;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.common.mapper.EventMapper;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.model.Event;
import dev.ime.domain.port.outbound.EventWriteRepositoryPort;
import dev.ime.domain.port.outbound.RequestByNameReadRepositoryPort;

@Component
public class CreatePublisherCommandHandler implements CommandHandler {

	private final EventWriteRepositoryPort eventWriteRepository;
	private final EventMapper eventMapper;
	private final RequestByNameReadRepositoryPort requestByNameReadRepositoryPublisher;	
	
	public CreatePublisherCommandHandler(EventWriteRepositoryPort eventWriteRepository, EventMapper eventMapper,
			@Qualifier("publisherReadRepositoryAdapter")RequestByNameReadRepositoryPort requestByNameReadRepositoryPublisher) {
		super();
		this.eventWriteRepository = eventWriteRepository;
		this.eventMapper = eventMapper;
		this.requestByNameReadRepositoryPublisher = requestByNameReadRepositoryPublisher;
	}

	@Override
	public Optional<Event> handle(Command command) {

		if (command instanceof CreatePublisherCommand createCommand) {
			
			validateCreate(createCommand);
			
			Event event = eventMapper.createEvent(GlobalConstants.PUBLI_CAT, GlobalConstants.PUBLI_CREATED, createCommand);
			return eventWriteRepository.save(event);
			
		} else {
			throw new IllegalArgumentException(command.getClass().toString());
		}		
	}

	private void validateCreate(CreatePublisherCommand createCommand) {
		
		checkExistsByName(createCommand.name());		
	}
	
	private void checkExistsByName(String name) {
		
		if (requestByNameReadRepositoryPublisher.existsByName(name)) {			
			throw new UniqueValueException(Map.of(GlobalConstants.PUBLI_NAME, name));			
		}		
	}
}
