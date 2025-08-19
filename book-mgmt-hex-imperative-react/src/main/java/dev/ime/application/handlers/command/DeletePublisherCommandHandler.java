package dev.ime.application.handlers.command;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

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
	public Optional<Event> handle(Command command) {

		if (command instanceof DeletePublisherCommand deleteCommand) {

			validateDelete(deleteCommand);
			
			Event event = eventMapper.createEvent(GlobalConstants.PUBLI_CAT, GlobalConstants.PUBLI_DELETED, deleteCommand);
			return eventWriteRepository.save(event);
			
		} else {
			throw new IllegalArgumentException(command.getClass().toString());
		}	
	}

	private void validateDelete(DeletePublisherCommand deleteCommand) {
		
		Long publisherId = deleteCommand.publisherId();
		Optional<Publisher> opt = publisherReadRepositoryAdapter.findById(publisherId);
		
		checkExistsPublisher(publisherId, opt);
		checkAssociatedEntity(opt.get());
	}
	
	private void checkExistsPublisher(Long publisherId, Optional<?> opt) {

		if (opt.isEmpty()) {
			throw new ResourceNotFoundException(Map.of(GlobalConstants.PUBLI_ID, String.valueOf(publisherId)));
		}	
	}

	private void checkAssociatedEntity(Publisher dom) {
				
		if (!dom.getBooks().isEmpty()) {
			throw new EntityAssociatedException(Map.of(GlobalConstants.PUBLI_ID, String.valueOf(dom.getPublisherId())));
		}
	}
}
