package dev.ime.application.handlers.command;

import java.util.Map;

import org.springframework.stereotype.Component;

import dev.ime.application.exception.CreateEventException;
import dev.ime.application.exception.EntityAssociatedException;
import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.usecases.command.DeleteGenreCommand;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.common.mapper.EventMapper;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.model.Event;
import dev.ime.domain.model.Genre;
import dev.ime.domain.port.outbound.EventWriteRepositoryPort;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import reactor.core.publisher.Mono;

@Component
public class DeleteGenreCommandHandler implements CommandHandler {

	private final EventWriteRepositoryPort eventWriteRepository;
	private final EventMapper eventMapper;
	private final ReadRepositoryPort<Genre> genreReadRepositoryAdapter;
	
	public DeleteGenreCommandHandler(EventWriteRepositoryPort eventWriteRepository, EventMapper eventMapper,
			ReadRepositoryPort<Genre> genreReadRepositoryAdapter) {
		super();
		this.eventWriteRepository = eventWriteRepository;
		this.eventMapper = eventMapper;
		this.genreReadRepositoryAdapter = genreReadRepositoryAdapter;
	}

	@Override
	public Mono<Event> handle(Command command) {
		
		return Mono.justOrEmpty(command)
				.ofType(DeleteGenreCommand.class)
				.flatMap(this::validateDelete)
				.map(this::createEvent)
				.flatMap(eventWriteRepository::save)
				.switchIfEmpty(Mono.error(new CreateEventException(Map.of(GlobalConstants.DELETE_GENRE, ""))));	
	}

	private Mono<DeleteGenreCommand> validateDelete(DeleteGenreCommand command) {
	    
		return Mono.just(command)
				.flatMap(this::validateIdExists)
				.flatMap(this::checkEntityAssociation)				
				.thenReturn(command);		
	}

	private Mono<Genre> validateIdExists(DeleteGenreCommand command) {

		return genreReadRepositoryAdapter.findById(command.genreId())
				.switchIfEmpty(Mono.error(new ResourceNotFoundException(Map.of(GlobalConstants.GENRE_ID, String.valueOf(command.genreId())))));
	}

	private Mono<Genre> checkEntityAssociation(Genre entity){
		
		return Mono.justOrEmpty(entity.getBooks())
				.filter( i -> i.isEmpty())
				.switchIfEmpty(Mono.error(new EntityAssociatedException(Map.of(GlobalConstants.GENRE_ID, String.valueOf(entity.getGenreId())))))
				.thenReturn(entity);
	}
	
	private Event createEvent(Command command) {		
		
		return eventMapper.createEvent(GlobalConstants.GENRE_CAT, GlobalConstants.GENRE_DELETED, command);
	}	
}
