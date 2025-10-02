package dev.ime.application.handlers.command;

import java.util.Map;

import org.springframework.stereotype.Component;

import dev.ime.application.exception.CreateEventException;
import dev.ime.application.exception.EntityAssociatedException;
import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.usecases.command.DeleteBookCommand;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.common.mapper.EventMapper;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.model.Book;
import dev.ime.domain.model.Event;
import dev.ime.domain.port.outbound.EventWriteRepositoryPort;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import reactor.core.publisher.Mono;

@Component
public class DeleteBookCommandHandler implements CommandHandler {

	private final EventWriteRepositoryPort eventWriteRepository;
	private final EventMapper eventMapper;
	private final ReadRepositoryPort<Book> bookReadRepositoryAdapter;
	
	public DeleteBookCommandHandler(EventWriteRepositoryPort eventWriteRepository, EventMapper eventMapper,
			ReadRepositoryPort<Book> bookReadRepositoryAdapter) {
		super();
		this.eventWriteRepository = eventWriteRepository;
		this.eventMapper = eventMapper;
		this.bookReadRepositoryAdapter = bookReadRepositoryAdapter;
	}	

	@Override
	public Mono<Event> handle(Command command) {
		
		return Mono.justOrEmpty(command)
				.ofType(DeleteBookCommand.class)
				.flatMap(this::validateDelete)
				.map(this::createEvent)
				.flatMap(eventWriteRepository::save)
				.switchIfEmpty(Mono.error(new CreateEventException(Map.of(GlobalConstants.DELETE_BOOK, ""))));	
	}

	private Mono<DeleteBookCommand> validateDelete(DeleteBookCommand command) {
	    
		return Mono.just(command)
				.flatMap(this::validateIdExists)
				.flatMap(this::checkEntityAssociation)				
				.thenReturn(command);		
	}

	private Mono<Book> validateIdExists(DeleteBookCommand command) {

		return bookReadRepositoryAdapter.findById(command.bookId())
				.switchIfEmpty(Mono.error(new ResourceNotFoundException(Map.of(GlobalConstants.BOOK_ID, String.valueOf(command.bookId())))));
	}

	private Mono<Book> checkEntityAssociation(Book entity){
		
		return Mono.justOrEmpty(entity.getBookshops())
				.filter( i -> i.isEmpty())
				.switchIfEmpty(Mono.error(new EntityAssociatedException(Map.of(GlobalConstants.BOOK_ID, String.valueOf(entity.getBookId())))))
				.thenReturn(entity);
	}
	
	private Event createEvent(Command command) {		
		
		return eventMapper.createEvent(GlobalConstants.BOOK_CAT, GlobalConstants.BOOK_DELETED, command);
	}	
}
