package dev.ime.application.handlers.command;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.exception.UniqueValueException;
import dev.ime.application.usecases.command.UpdateGenreCommand;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.common.mapper.EventMapper;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.model.Event;
import dev.ime.domain.model.Genre;
import dev.ime.domain.port.outbound.EventWriteRepositoryPort;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.domain.port.outbound.RequestByNameReadRepositoryPort;

@Component
public class UpdateGenreCommandHandler implements CommandHandler {

	private final EventWriteRepositoryPort eventWriteRepository;
	private final EventMapper eventMapper;
	private final ReadRepositoryPort<Genre> genreReadRepositoryAdapter;
	private final RequestByNameReadRepositoryPort requestByNameReadRepositoryGenre;
	
	public UpdateGenreCommandHandler(EventWriteRepositoryPort eventWriteRepository, EventMapper eventMapper,
			ReadRepositoryPort<Genre> genreReadRepositoryAdapter,
			@Qualifier("genreReadRepositoryAdapter")RequestByNameReadRepositoryPort requestByNameReadRepositoryGenre) {
		super();
		this.eventWriteRepository = eventWriteRepository;
		this.eventMapper = eventMapper;
		this.genreReadRepositoryAdapter = genreReadRepositoryAdapter;
		this.requestByNameReadRepositoryGenre = requestByNameReadRepositoryGenre;
	}

	@Override
	public Optional<Event> handle(Command command) {

		if (command instanceof UpdateGenreCommand updateCommand) {

			validateUpdate(updateCommand);
			
			Event event = eventMapper.createEvent(GlobalConstants.GENRE_CAT, GlobalConstants.GENRE_UPDATED, updateCommand);
			return eventWriteRepository.save(event);
			
		} else {
			throw new IllegalArgumentException(command.getClass().toString());
		}	
	}

	private void validateUpdate(UpdateGenreCommand updateCommand) {
		
		Long genreId = updateCommand.genreId();
		Optional<Genre> opt = genreReadRepositoryAdapter.findById(genreId);
		
		checkExistsGenre(genreId, opt);
		checkExistsByName(updateCommand.name(), genreId); 
	}
	
	private void checkExistsGenre(Long genreId, Optional<?> opt) {

		if (opt.isEmpty()) {
			throw new ResourceNotFoundException(Map.of(GlobalConstants.GENRE_ID, String.valueOf(genreId)));
		}	
	}

	private void checkExistsByName(String name, Long genreId) {
		
		if (requestByNameReadRepositoryGenre.findByNameAndIdNot(name, genreId)) {			
			throw new UniqueValueException(Map.of(GlobalConstants.GENRE_NAME, name));			
		}		
	}
}
