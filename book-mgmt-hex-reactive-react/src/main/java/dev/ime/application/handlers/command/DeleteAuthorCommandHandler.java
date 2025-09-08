package dev.ime.application.handlers.command;

import java.util.Map;

import org.springframework.stereotype.Component;

import dev.ime.application.exception.CreateEventException;
import dev.ime.application.exception.EntityAssociatedException;
import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.usecases.command.DeleteAuthorCommand;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.common.mapper.EventMapper;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.model.Author;
import dev.ime.domain.model.Event;
import dev.ime.domain.port.outbound.EventWriteRepositoryPort;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import reactor.core.publisher.Mono;

@Component
public class DeleteAuthorCommandHandler implements CommandHandler {

	private final EventWriteRepositoryPort eventWriteRepository;
	private final EventMapper eventMapper;
	private final ReadRepositoryPort<Author> authorReadRepositoryAdapter;
	
	public DeleteAuthorCommandHandler(EventWriteRepositoryPort eventWriteRepository, EventMapper eventMapper,
			ReadRepositoryPort<Author> authorReadRepositoryAdapter) {
		super();
		this.eventWriteRepository = eventWriteRepository;
		this.eventMapper = eventMapper;
		this.authorReadRepositoryAdapter = authorReadRepositoryAdapter;
	}

	@Override
	public Mono<Event> handle(Command command) {
		
		return Mono.justOrEmpty(command)
				.ofType(DeleteAuthorCommand.class)
				.flatMap(this::validateDelete)
				.map(this::createEvent)
				.flatMap(eventWriteRepository::save)
				.switchIfEmpty(Mono.error(new CreateEventException(Map.of(GlobalConstants.DELETE_AUTHOR, ""))));	
	}

	private Mono<DeleteAuthorCommand> validateDelete(DeleteAuthorCommand command) {
	    
		return Mono.just(command)
				.flatMap(this::validateIdExists)
				.flatMap(this::checkEntityAssociation)				
				.thenReturn(command);		
	}

	private Mono<Author> validateIdExists(DeleteAuthorCommand command) {

		return authorReadRepositoryAdapter.findById(command.authorId())
				.switchIfEmpty(Mono.error(new ResourceNotFoundException(Map.of(GlobalConstants.AUTHOR_ID, String.valueOf(command.authorId())))));
	}

	private Mono<Author> checkEntityAssociation(Author entity){
		
		return Mono.justOrEmpty(entity.getBooks())
				.filter( i -> i.isEmpty())
				.switchIfEmpty(Mono.error(new EntityAssociatedException(Map.of(GlobalConstants.AUTHOR_ID, String.valueOf(entity.getAuthorId())))))
				.thenReturn(entity);
	}
	
	private Event createEvent(Command command) {		
		
		return eventMapper.createEvent(GlobalConstants.AUTHOR_CAT, GlobalConstants.AUTHOR_DELETED, command);
	}	
}
