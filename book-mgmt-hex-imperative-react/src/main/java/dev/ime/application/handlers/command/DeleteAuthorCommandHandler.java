package dev.ime.application.handlers.command;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

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
	public Optional<Event> handle(Command command) {
		
		if (command instanceof DeleteAuthorCommand deleteCommand) {

			validateDelete(deleteCommand);
			
			Event event = eventMapper.createEvent(GlobalConstants.AUTHOR_CAT, GlobalConstants.AUTHOR_DELETED, deleteCommand);
			return eventWriteRepository.save(event);
			
		} else {
			throw new IllegalArgumentException(command.getClass().toString());
		}	
	}

	private void validateDelete(DeleteAuthorCommand deleteCommand) {
		
		Long authorId = deleteCommand.authorId();
		Optional<Author> opt = authorReadRepositoryAdapter.findById(authorId);
		
		checkExistsAuthor(authorId, opt);
		checkAssociatedEntity(opt.get());
	}
	
	private void checkExistsAuthor(Long authorId, Optional<?> opt) {

		if (opt.isEmpty()) {
			throw new ResourceNotFoundException(Map.of(GlobalConstants.AUTHOR_ID, String.valueOf(authorId)));
		}	
	}

	private void checkAssociatedEntity(Author dom) {
				
		if (!dom.getBooks().isEmpty()) {
			throw new EntityAssociatedException(Map.of(GlobalConstants.AUTHOR_ID, String.valueOf(dom.getAuthorId())));
		}
	}
}
