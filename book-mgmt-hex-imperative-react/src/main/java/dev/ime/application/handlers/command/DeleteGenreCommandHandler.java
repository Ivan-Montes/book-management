package dev.ime.application.handlers.command;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

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
	public Optional<Event> handle(Command command) {

		if (command instanceof DeleteGenreCommand deleteCommand) {

			validateDelete(deleteCommand);
			
			Event event = eventMapper.createEvent(GlobalConstants.GENRE_CAT, GlobalConstants.GENRE_DELETED, deleteCommand);
			return eventWriteRepository.save(event);
			
		} else {
			throw new IllegalArgumentException(command.getClass().toString());
		}	
	}

	private void validateDelete(DeleteGenreCommand deleteCommand) {
		
		Long genreId = deleteCommand.genreId();
		Optional<Genre> opt = genreReadRepositoryAdapter.findById(genreId);
		
		checkExistsGenre(genreId, opt);
		checkAssociatedEntity(opt.get());
	}
	
	private void checkExistsGenre(Long genreId, Optional<?> opt) {

		if (opt.isEmpty()) {
			throw new ResourceNotFoundException(Map.of(GlobalConstants.GENRE_ID, String.valueOf(genreId)));
		}	
	}

	private void checkAssociatedEntity(Genre dom) {
				
		if (!dom.getBooks().isEmpty()) {
			throw new EntityAssociatedException(Map.of(GlobalConstants.GENRE_ID, String.valueOf(dom.getGenreId())));
		}
	}
}
