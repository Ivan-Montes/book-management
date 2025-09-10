package dev.ime.application.service;

import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import dev.ime.application.dto.BookBookshopDto;
import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.usecases.command.CreateBookBookshopCommand;
import dev.ime.application.usecases.command.DeleteBookBookshopCommand;
import dev.ime.application.usecases.command.UpdateBookBookshopCommand;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.domain.command.Command;
import dev.ime.domain.model.Event;
import dev.ime.domain.port.inbound.CommandDispatcher;
import dev.ime.domain.port.inbound.CompositeIdCommandServicePort;
import dev.ime.domain.port.outbound.PublisherPort;
import reactor.core.publisher.Mono;

@Service
public class BookBookshopCommandService implements CompositeIdCommandServicePort<BookBookshopDto> {

	private final CommandDispatcher commandDispatcher;
	private final PublisherPort publisherPort;
	private static final Logger logger = LoggerFactory.getLogger(BookBookshopCommandService.class);
	
	public BookBookshopCommandService(@Qualifier("bookBookshopCommandDispatcher")CommandDispatcher commandDispatcher, PublisherPort publisherPort) {
		super();
		this.commandDispatcher = commandDispatcher;
		this.publisherPort = publisherPort;
	}

	@Override
	public Mono<Event> create(BookBookshopDto item) {

		return Mono.justOrEmpty(item)
				.switchIfEmpty(Mono.error(new ResourceNotFoundException(Map.of(GlobalConstants.BBS_CAT, GlobalConstants.MSG_REQUIRED))))						
				.map(dto -> new CreateBookBookshopCommand(dto.bookId(), dto.bookshopId(), dto.price(), dto.units()))
				.flatMap(this::runHandler)
				.flatMap(this::processEvents);
	}

	@Override
	public Mono<Event> update(UUID id01, UUID id02, BookBookshopDto item) {

		return Mono.justOrEmpty(item)
				.switchIfEmpty(Mono.error(new ResourceNotFoundException(Map.of(GlobalConstants.BBS_CAT, GlobalConstants.MSG_REQUIRED))))
				.map(dto -> new UpdateBookBookshopCommand(id01, id02, dto.price(), dto.units()))
				.flatMap(this::runHandler)
				.flatMap(this::processEvents);
	}

	@Override
	public Mono<Event> deleteById(UUID id01, UUID id02) {
		
		return Mono.just(new DeleteBookBookshopCommand(id01, id02))
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
