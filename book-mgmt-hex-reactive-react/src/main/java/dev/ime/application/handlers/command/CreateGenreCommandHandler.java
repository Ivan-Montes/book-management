package dev.ime.application.handlers.command;

import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import dev.ime.application.exception.CreateEventException;
import dev.ime.application.exception.UniqueValueException;
import dev.ime.application.usecases.command.CreateGenreCommand;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.common.mapper.EventMapper;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.model.Event;
import dev.ime.domain.port.outbound.EventWriteRepositoryPort;
import dev.ime.domain.port.outbound.RequestByNameReadRepositoryPort;
import reactor.core.publisher.Mono;

@Component
public class CreateGenreCommandHandler implements CommandHandler {

	private final EventWriteRepositoryPort eventWriteRepository;
	private final EventMapper eventMapper;
	private final RequestByNameReadRepositoryPort requestByNameReadRepositoryGenre;
	
	public CreateGenreCommandHandler(EventWriteRepositoryPort eventWriteRepository, EventMapper eventMapper,
			@Qualifier("genreReadRepositoryAdapter")RequestByNameReadRepositoryPort requestByNameReadRepositoryGenre) {
		super();
		this.eventWriteRepository = eventWriteRepository;
		this.eventMapper = eventMapper;
		this.requestByNameReadRepositoryGenre = requestByNameReadRepositoryGenre;
	}

	@Override
	public Mono<Event> handle(Command command) {

		return Mono.justOrEmpty(command)
				.ofType(CreateGenreCommand.class)
				.flatMap(this::validateCreate)
				.map(this::createEvent)
				.flatMap(eventWriteRepository::save)
				.switchIfEmpty(Mono.error(new CreateEventException(Map.of(GlobalConstants.CREATE_GENRE, ""))));	
	}

	private Mono<CreateGenreCommand> validateCreate(CreateGenreCommand command) {
		
		return Mono.justOrEmpty(command)
				.flatMap(this::checkExistsByName)
				.thenReturn(command);
	}

	private Mono<CreateGenreCommand> checkExistsByName(CreateGenreCommand command) {
	    return requestByNameReadRepositoryGenre.existsByName(command.name())
	    		.filter(b -> !b)
	    		.switchIfEmpty(Mono.error(new UniqueValueException(Map.of(GlobalConstants.GENRE_NAME, command.name()))))
	    		.thenReturn(command);
	}
	
	private Event createEvent(Command command) {		
		return eventMapper.createEvent(GlobalConstants.GENRE_CAT, GlobalConstants.GENRE_CREATED, command);
	}
}
