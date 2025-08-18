package dev.ime.application.handlers.command;

import java.util.Optional;

import org.springframework.stereotype.Component;

import dev.ime.application.usecases.command.CreateAuthorCommand;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.common.mapper.EventMapper;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.model.Event;
import dev.ime.domain.port.outbound.EventWriteRepositoryPort;

@Component
public class CreateAuthorCommandHandler implements CommandHandler {

	private final EventWriteRepositoryPort eventWriteRepository;
	private final EventMapper eventMapper;
	
	public CreateAuthorCommandHandler(EventWriteRepositoryPort eventWriteRepository, EventMapper eventMapper) {
		super();
		this.eventWriteRepository = eventWriteRepository;
		this.eventMapper = eventMapper;
	}

	@Override
	public Optional<Event> handle(Command command) {
		
		if (command instanceof CreateAuthorCommand createCommand) {
			
			Event event = eventMapper.createEvent(GlobalConstants.AUTHOR_CAT, GlobalConstants.AUTHOR_CREATED, createCommand);
			return eventWriteRepository.save(event);
			
		} else {
			throw new IllegalArgumentException(command.getClass().toString());
		}		
	}
}
