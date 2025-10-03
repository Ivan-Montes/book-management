package dev.ime.application.service;

import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import dev.ime.application.dto.AuthorDto;
import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.usecases.command.CreateAuthorCommand;
import dev.ime.application.usecases.command.DeleteAuthorCommand;
import dev.ime.application.usecases.command.UpdateAuthorCommand;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.domain.command.Command;
import dev.ime.domain.model.Event;
import dev.ime.domain.port.inbound.CommandDispatcher;
import dev.ime.domain.port.inbound.CommandServicePort;
import dev.ime.domain.port.outbound.PublisherPort;
import reactor.core.publisher.Mono;

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
	public Mono<Event> create(AuthorDto item) {

		return Mono.justOrEmpty(item)
				.switchIfEmpty(Mono.error(new ResourceNotFoundException(Map.of(GlobalConstants.AUTHOR_CAT, GlobalConstants.MSG_REQUIRED))))						
				.map(dto -> new CreateAuthorCommand(UUID.randomUUID(), dto.name(), dto.surname()))
				.flatMap(this::runHandler)
				.flatMap(this::processEvents);	
	}

	@Override
	public Mono<Event> update(UUID id, AuthorDto item) {
		
		return Mono.justOrEmpty(item)
				.switchIfEmpty(Mono.error(new ResourceNotFoundException(Map.of(GlobalConstants.AUTHOR_CAT, GlobalConstants.MSG_REQUIRED))))						
				.map(dto -> new UpdateAuthorCommand(id, dto.name(), dto.surname()))
				.flatMap(this::runHandler)
				.flatMap(this::processEvents);	
	}

	@Override
	public Mono<Event> deleteById(UUID id) {

		return Mono.justOrEmpty(id)
				.switchIfEmpty(Mono.error(new ResourceNotFoundException(Map.of(GlobalConstants.AUTHOR_CAT, GlobalConstants.MSG_REQUIRED))))						
				.map(DeleteAuthorCommand::new)
				.flatMap(this::runHandler)
				.flatMap(this::processEvents);	
	}

	private Mono<Event> runHandler(Command command){
		
		return Mono.just(command)
				.map(commandDispatcher::getCommandHandler)
				.flatMap( handler -> handler.handle(command));		
	}
	
	private Mono<Event> processEvents(Event event) {
		
	    return Mono.just(event)	
	        .doOnNext(eventItem -> logger.info(GlobalConstants.MSG_PATTERN_INFO, GlobalConstants.EVENT_CAT, eventItem))
	    .flatMap(eventItem -> publisherPort.publishEvent(event).thenReturn(event))
        .thenReturn(event);
	}
}
