package dev.ime.application.handlers.command;

import java.util.Map;

import org.springframework.stereotype.Component;

import dev.ime.application.exception.CreateEventException;
import dev.ime.application.usecases.command.CreateAuthorCommand;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.common.mapper.EventMapper;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.model.Event;
import dev.ime.domain.port.outbound.EventWriteRepositoryPort;
import reactor.core.publisher.Mono;

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
	public Mono<Event> handle(Command command) {

		return Mono.justOrEmpty(command)
				.ofType(CreateAuthorCommand.class)
				.map(this::createEvent)
				.flatMap(eventWriteRepository::save)
				.switchIfEmpty(Mono.error(new CreateEventException(Map.of(GlobalConstants.CREATE_AUTHOR, ""))));	
	}
	
	private Event createEvent(Command command) {		
		return eventMapper.createEvent(GlobalConstants.AUTHOR_CAT, GlobalConstants.AUTHOR_CREATED, command);
	}
}
