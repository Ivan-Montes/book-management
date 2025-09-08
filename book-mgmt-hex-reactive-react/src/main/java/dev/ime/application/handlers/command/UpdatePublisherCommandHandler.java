package dev.ime.application.handlers.command;

import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import dev.ime.application.exception.CreateEventException;
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
import reactor.core.publisher.Mono;

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
	public Mono<Event> handle(Command command) {
		
		return Mono.justOrEmpty(command)
				.ofType(UpdatePublisherCommand.class)
				.flatMap(this::validateUpdate)
				.map(this::createEvent)
				.flatMap(eventWriteRepository::save)
				.switchIfEmpty(Mono.error(new CreateEventException(Map.of(GlobalConstants.UPDATE_PUBLI, ""))));	
	}

	private Mono<UpdatePublisherCommand> validateUpdate(UpdatePublisherCommand command) {
		
		return Mono.justOrEmpty(command)
				.flatMap(this::checkExistsPublisher)
				.flatMap(this::checkExistsByName)
				.thenReturn(command);
	}

	private Mono<UpdatePublisherCommand> checkExistsPublisher(UpdatePublisherCommand command) {

		return publisherReadRepositoryAdapter.findById(command.publisherId())
				.switchIfEmpty(Mono.error(new ResourceNotFoundException(
						Map.of(GlobalConstants.PUBLI_ID, String.valueOf(command.publisherId())))))
	    		.thenReturn(command);
	}

	private Mono<UpdatePublisherCommand> checkExistsByName(UpdatePublisherCommand command) {
		return requestByNameReadRepositoryPublisher.findByNameAndIdNot(command.name(), command.publisherId())
	    		.filter(b -> !b)
	    		.switchIfEmpty(Mono.error(new UniqueValueException(
	    				Map.of(GlobalConstants.PUBLI_NAME, command.name()))))
	    		.thenReturn(command);
	}

	private Event createEvent(Command command) {		
		return eventMapper.createEvent(GlobalConstants.PUBLI_CAT, GlobalConstants.PUBLI_UPDATED, command);
	}
}
