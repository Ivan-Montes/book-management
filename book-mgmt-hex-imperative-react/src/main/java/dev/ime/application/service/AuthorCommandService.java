package dev.ime.application.service;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import dev.ime.application.dto.AuthorDto;
import dev.ime.application.exception.EventUnexpectedException;
import dev.ime.application.usecases.command.CreateAuthorCommand;
import dev.ime.application.usecases.command.DeleteAuthorCommand;
import dev.ime.application.usecases.command.UpdateAuthorCommand;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.model.Event;
import dev.ime.domain.port.inbound.CommandDispatcher;
import dev.ime.domain.port.inbound.CommandServicePort;
import dev.ime.domain.port.outbound.PublisherPort;

@Service
public class AuthorCommandService implements CommandServicePort<AuthorDto> {

	private final CommandDispatcher commandDispatcher;
	private final PublisherPort publisherPort;
	private static final Logger logger = LoggerFactory.getLogger(AuthorCommandService.class);
	
	public AuthorCommandService(@Qualifier("authorCommandDispatcher")CommandDispatcher commandDispatcher, PublisherPort publisherPort) {
		super();
		this.commandDispatcher = commandDispatcher;
		this.publisherPort = publisherPort;
	}

	@Override
	public Optional<Event> create(AuthorDto item) {
		
		CreateAuthorCommand createCommand = new CreateAuthorCommand(item.name(), item.surname());
		
		return handleCommand(createCommand);
	}

	@Override
	public Optional<Event> update(Long id, AuthorDto item) {

		UpdateAuthorCommand updateCommand = new UpdateAuthorCommand(id, item.name(), item.surname());
		
		return handleCommand(updateCommand);
	}

	@Override
	public Optional<Event> deleteById(Long id) {

		DeleteAuthorCommand deleteCommand = new DeleteAuthorCommand(id);
		
		return handleCommand(deleteCommand);
	}
	
	private Optional<Event> handleCommand(Command command) {

		CommandHandler handler = commandDispatcher.getCommandHandler(command);
		Optional<Event> optEvent = handler.handle(command);
        publishEventIfPresent(optEvent);
		
		return optEvent;
	}
	
	private void publishEventIfPresent(Optional<Event> optEvent) {
		
		if ( optEvent.isPresent() ) {
			
			logger.info(GlobalConstants.MSG_PATTERN_INFO, GlobalConstants.EVENT_CAT, optEvent.get());
			publisherPort.publishEvent(optEvent.get());
			
		} else {
			throw new EventUnexpectedException(Map.of(GlobalConstants.EVENT_CAT, GlobalConstants.MSG_NODATA));
		}
	}
}
