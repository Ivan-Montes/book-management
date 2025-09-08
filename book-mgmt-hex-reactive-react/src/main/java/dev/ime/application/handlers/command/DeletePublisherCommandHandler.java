package dev.ime.application.handlers.command;

import java.util.Map;

import org.springframework.stereotype.Component;

import dev.ime.application.exception.CreateEventException;
import dev.ime.application.exception.EntityAssociatedException;
import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.usecases.command.DeletePublisherCommand;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.common.mapper.EventMapper;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.model.Event;
import dev.ime.domain.model.Publisher;
import dev.ime.domain.port.outbound.EventWriteRepositoryPort;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import reactor.core.publisher.Mono;

@Component
public class DeletePublisherCommandHandler implements CommandHandler {

	private final EventWriteRepositoryPort eventWriteRepository;
	private final EventMapper eventMapper;
	private final ReadRepositoryPort<Publisher> publisherReadRepositoryAdapter;
	
	public DeletePublisherCommandHandler(EventWriteRepositoryPort eventWriteRepository, EventMapper eventMapper,
			ReadRepositoryPort<Publisher> publisherReadRepositoryAdapter) {
		super();
		this.eventWriteRepository = eventWriteRepository;
		this.eventMapper = eventMapper;
		this.publisherReadRepositoryAdapter = publisherReadRepositoryAdapter;
	}

	@Override
	public Mono<Event> handle(Command command) {
		
		return Mono.justOrEmpty(command)
				.ofType(DeletePublisherCommand.class)
				.flatMap(this::validateDelete)
				.map(this::createEvent)
				.flatMap(eventWriteRepository::save)
				.switchIfEmpty(Mono.error(new CreateEventException(Map.of(GlobalConstants.DELETE_PUBLI, ""))));	
	}

	private Mono<DeletePublisherCommand> validateDelete(DeletePublisherCommand command) {
	    
		return Mono.just(command)
				.flatMap(this::validateIdExists)
				.flatMap(this::checkEntityAssociation)				
				.thenReturn(command);		
	}

	private Mono<Publisher> validateIdExists(DeletePublisherCommand command) {

		return publisherReadRepositoryAdapter.findById(command.publisherId())
				.switchIfEmpty(Mono.error(new ResourceNotFoundException(Map.of(GlobalConstants.PUBLI_ID, String.valueOf(command.publisherId())))));
	}

	private Mono<Publisher> checkEntityAssociation(Publisher entity){
		
		return Mono.justOrEmpty(entity.getBooks())
				.filter( i -> i.isEmpty())
				.switchIfEmpty(Mono.error(new EntityAssociatedException(Map.of(GlobalConstants.PUBLI_ID, String.valueOf(entity.getPublisherId())))))
				.thenReturn(entity);
	}
	
	private Event createEvent(Command command) {		
		
		return eventMapper.createEvent(GlobalConstants.PUBLI_CAT, GlobalConstants.PUBLI_DELETED, command);
	}	
}
