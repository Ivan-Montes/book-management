package dev.ime.application.service;

import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import dev.ime.application.dto.GenreDto;
import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.usecases.command.CreateGenreCommand;
import dev.ime.application.usecases.command.DeleteGenreCommand;
import dev.ime.application.usecases.command.UpdateGenreCommand;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.domain.command.Command;
import dev.ime.domain.model.Event;
import dev.ime.domain.port.inbound.CommandDispatcher;
import dev.ime.domain.port.inbound.CommandServicePort;
import dev.ime.domain.port.outbound.PublisherPort;
import reactor.core.publisher.Mono;

@Service
public class GenreCommandService implements CommandServicePort<GenreDto> {

	private final CommandDispatcher commandDispatcher;
	private final PublisherPort publisherPort;
	private static final Logger logger = LoggerFactory.getLogger(GenreCommandService.class);
	
	public GenreCommandService(@Qualifier("genreCommandDispatcher")CommandDispatcher commandDispatcher, PublisherPort publisherPort) {
		super();
		this.commandDispatcher = commandDispatcher;
		this.publisherPort = publisherPort;
	}

	@Override
	public Mono<Event> create(GenreDto item) {

		return Mono.justOrEmpty(item)
				.switchIfEmpty(Mono.error(new ResourceNotFoundException(Map.of(GlobalConstants.GENRE_CAT, GlobalConstants.MSG_REQUIRED))))						
				.map(dto -> new CreateGenreCommand(UUID.randomUUID(), dto.name(), dto.description()))
				.flatMap(this::runHandler)
				.flatMap(this::processEvents);	
	}

	@Override
	public Mono<Event> update(UUID id, GenreDto item) {
		
		return Mono.justOrEmpty(item)
				.switchIfEmpty(Mono.error(new ResourceNotFoundException(Map.of(GlobalConstants.GENRE_CAT, GlobalConstants.MSG_REQUIRED))))						
				.map(dto -> new UpdateGenreCommand(id, dto.name(), dto.description()))
				.flatMap(this::runHandler)
				.flatMap(this::processEvents);	
	}

	@Override
	public Mono<Event> deleteById(UUID id) {

		return Mono.justOrEmpty(id)
				.switchIfEmpty(Mono.error(new ResourceNotFoundException(Map.of(GlobalConstants.GENRE_CAT, GlobalConstants.MSG_REQUIRED))))						
				.map(DeleteGenreCommand::new)
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
