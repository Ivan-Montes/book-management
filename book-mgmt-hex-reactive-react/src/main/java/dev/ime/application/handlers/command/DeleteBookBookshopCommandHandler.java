package dev.ime.application.handlers.command;

import java.util.Map;

import org.springframework.stereotype.Component;

import dev.ime.application.exception.CreateEventException;
import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.usecases.command.DeleteBookBookshopCommand;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.common.mapper.EventMapper;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.model.BookBookshop;
import dev.ime.domain.model.Event;
import dev.ime.domain.port.outbound.CompositeIdReadRepositoryPort;
import dev.ime.domain.port.outbound.EventWriteRepositoryPort;
import reactor.core.publisher.Mono;

@Component
public class DeleteBookBookshopCommandHandler implements CommandHandler {

	private final EventWriteRepositoryPort eventWriteRepository;
	private final EventMapper eventMapper;
	private final CompositeIdReadRepositoryPort<BookBookshop> bookBookshopReadRepositoryAdapter;
	
	public DeleteBookBookshopCommandHandler(EventWriteRepositoryPort eventWriteRepository, EventMapper eventMapper,
			CompositeIdReadRepositoryPort<BookBookshop> bookBookshopReadRepositoryAdapter) {
		super();
		this.eventWriteRepository = eventWriteRepository;
		this.eventMapper = eventMapper;
		this.bookBookshopReadRepositoryAdapter = bookBookshopReadRepositoryAdapter;
	}

	@Override
	public Mono<Event> handle(Command command) {
		
		return Mono.justOrEmpty(command)
				.ofType(DeleteBookBookshopCommand.class)
				.flatMap(this::validateDelete)
				.map(this::createEvent)
				.flatMap(eventWriteRepository::save)
				.switchIfEmpty(Mono.error(new CreateEventException(Map.of(GlobalConstants.DELETE_BBS, ""))));	
	}

	private Mono<DeleteBookBookshopCommand> validateDelete(DeleteBookBookshopCommand command) {
	    
		return Mono.just(command)
				.flatMap(this::validateIdExists)
				.thenReturn(command);		
	}

	private Mono<BookBookshop> validateIdExists(DeleteBookBookshopCommand command) {

		return bookBookshopReadRepositoryAdapter.findById(command.bookId(), command.bookshopId())
				.switchIfEmpty(Mono.error(
						new ResourceNotFoundException(Map.of(GlobalConstants.BOOK_ID, String.valueOf(command.bookId()),
								GlobalConstants.BS_ID, String.valueOf(command.bookshopId())))));
	}

	private Event createEvent(Command command) {		
		
		return eventMapper.createEvent(GlobalConstants.BBS_CAT, GlobalConstants.BBS_DELETED, command);
	}		
}
