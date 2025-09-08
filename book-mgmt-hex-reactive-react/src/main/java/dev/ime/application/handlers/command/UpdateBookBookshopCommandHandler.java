package dev.ime.application.handlers.command;

import java.util.Map;

import org.springframework.stereotype.Component;

import dev.ime.application.exception.CreateEventException;
import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.usecases.command.UpdateBookBookshopCommand;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.common.mapper.EventMapper;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.model.Book;
import dev.ime.domain.model.BookBookshop;
import dev.ime.domain.model.Bookshop;
import dev.ime.domain.model.Event;
import dev.ime.domain.port.outbound.CompositeIdReadRepositoryPort;
import dev.ime.domain.port.outbound.EventWriteRepositoryPort;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import reactor.core.publisher.Mono;

@Component
public class UpdateBookBookshopCommandHandler implements CommandHandler {

	private final EventWriteRepositoryPort eventWriteRepository;
	private final EventMapper eventMapper;
	private final ReadRepositoryPort<Book> bookReadRepositoryAdapter;
	private final ReadRepositoryPort<Bookshop> bookshopReadRepositoryAdapter;
	private final CompositeIdReadRepositoryPort<BookBookshop> bookBookshopReadRepositoryAdapter;

	public UpdateBookBookshopCommandHandler(EventWriteRepositoryPort eventWriteRepository, EventMapper eventMapper,
			ReadRepositoryPort<Book> bookReadRepositoryAdapter,
			ReadRepositoryPort<Bookshop> bookshopReadRepositoryAdapter,
			CompositeIdReadRepositoryPort<BookBookshop> bookBookshopReadRepositoryAdapter) {
		super();
		this.eventWriteRepository = eventWriteRepository;
		this.eventMapper = eventMapper;
		this.bookReadRepositoryAdapter = bookReadRepositoryAdapter;
		this.bookshopReadRepositoryAdapter = bookshopReadRepositoryAdapter;
		this.bookBookshopReadRepositoryAdapter = bookBookshopReadRepositoryAdapter;
	}

	@Override
	public Mono<Event> handle(Command command) {

		return Mono.justOrEmpty(command)
				.ofType(UpdateBookBookshopCommand.class)
				.flatMap(this::validateUpdate)
				.map(this::createEvent).flatMap(eventWriteRepository::save)
				.switchIfEmpty(Mono.error(new CreateEventException(Map.of(GlobalConstants.UPDATE_BBS, ""))));
	}

	private Mono<UpdateBookBookshopCommand> validateUpdate(UpdateBookBookshopCommand command) {

		return Mono.justOrEmpty(command).flatMap(this::checkExistsBookById).flatMap(this::checkExistsBookshopById)
				.flatMap(this::checkExistsBookBookshopById).thenReturn(command);
	}

	private Mono<UpdateBookBookshopCommand> checkExistsBookById(UpdateBookBookshopCommand command) {
		return bookReadRepositoryAdapter.findById(command.bookId())
				.switchIfEmpty(Mono.error(new ResourceNotFoundException(Map.of(GlobalConstants.BOOK_ID, String.valueOf(command.bookId())))))
				.thenReturn(command);
	}

	private Mono<UpdateBookBookshopCommand> checkExistsBookshopById(UpdateBookBookshopCommand command) {
		return bookshopReadRepositoryAdapter.findById(command.bookshopId())
				.switchIfEmpty(Mono.error(new ResourceNotFoundException(Map.of(GlobalConstants.BS_ID, String.valueOf(command.bookshopId())))))
				.thenReturn(command);
	}

	private Mono<UpdateBookBookshopCommand> checkExistsBookBookshopById(UpdateBookBookshopCommand command) {
		return bookBookshopReadRepositoryAdapter.findById(command.bookId(), command.bookshopId())
				.switchIfEmpty(Mono.error(
						new ResourceNotFoundException(Map.of(GlobalConstants.BOOK_ID, String.valueOf(command.bookId()),
								GlobalConstants.BS_ID, String.valueOf(command.bookshopId())))))
				.thenReturn(command);
	}

	private Event createEvent(Command command) {
		return eventMapper.createEvent(GlobalConstants.BBS_CAT, GlobalConstants.BBS_UPDATED, command);
	}
}
