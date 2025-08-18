package dev.ime.application.handlers.command;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import dev.ime.application.exception.UniqueValueException;
import dev.ime.application.usecases.command.CreateGenreCommand;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.common.mapper.EventMapper;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.model.Event;
import dev.ime.domain.port.outbound.EventWriteRepositoryPort;
import dev.ime.domain.port.outbound.RequestByNameReadRepositoryPort;

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
	public Optional<Event> handle(Command command) {
		
		if (command instanceof CreateGenreCommand createCommand) {
			
			validateCreate(createCommand);
			
			Event event = eventMapper.createEvent(GlobalConstants.GENRE_CAT, GlobalConstants.GENRE_CREATED, createCommand);
			return eventWriteRepository.save(event);
			
		} else {
			throw new IllegalArgumentException(command.getClass().toString());
		}		
	}

	private void validateCreate(CreateGenreCommand createCommand) {
		
		checkExistsByName(createCommand.name());		
	}
	
	private void checkExistsByName(String name) {
		
		if (requestByNameReadRepositoryGenre.existsByName(name)) {			
			throw new UniqueValueException(Map.of(GlobalConstants.GENRE_NAME, name));			
		}		
	}
}
