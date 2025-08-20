package dev.ime.application.service;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import dev.ime.application.dto.PublisherDto;
import dev.ime.application.exception.EventUnexpectedException;
import dev.ime.application.usecases.command.CreatePublisherCommand;
import dev.ime.application.usecases.command.DeletePublisherCommand;
import dev.ime.application.usecases.command.UpdatePublisherCommand;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.model.Event;
import dev.ime.domain.port.inbound.CommandDispatcher;
import dev.ime.domain.port.inbound.CommandServicePort;
import dev.ime.domain.port.outbound.PublisherPort;

@Service
public class PublisherCommandService implements CommandServicePort<PublisherDto> {

	private final CommandDispatcher commandDispatcher;
	private final PublisherPort publisherPort;
	private static final Logger logger = LoggerFactory.getLogger(PublisherCommandService.class);
	
	public PublisherCommandService(@Qualifier("publisherCommandDispatcher")CommandDispatcher commandDispatcher, PublisherPort publisherPort) {
		super();
		this.commandDispatcher = commandDispatcher;
		this.publisherPort = publisherPort;
	}

	@Override
	public Optional<Event> create(PublisherDto item) {

		CreatePublisherCommand createCommand = new CreatePublisherCommand(item.name());
		
		return handleCommand(createCommand);
	}

	@Override
	public Optional<Event> update(Long id, PublisherDto item) {

		UpdatePublisherCommand updateCommand = new UpdatePublisherCommand(id, item.name());
		
		return handleCommand(updateCommand);
	}

	@Override
	public Optional<Event> deleteById(Long id) {

		DeletePublisherCommand deleteCommand = new DeletePublisherCommand(id);
		
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
