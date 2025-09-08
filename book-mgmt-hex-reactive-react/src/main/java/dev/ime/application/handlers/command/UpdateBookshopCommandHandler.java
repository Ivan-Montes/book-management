package dev.ime.application.handlers.command;

import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import dev.ime.application.exception.CreateEventException;
import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.exception.UniqueValueException;
import dev.ime.application.usecases.command.UpdateBookshopCommand;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.common.mapper.EventMapper;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.model.Bookshop;
import dev.ime.domain.model.Event;
import dev.ime.domain.port.outbound.EventWriteRepositoryPort;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.domain.port.outbound.RequestByNameReadRepositoryPort;
import reactor.core.publisher.Mono;

@Component
public class UpdateBookshopCommandHandler implements CommandHandler {

	private final EventWriteRepositoryPort eventWriteRepository;
	private final EventMapper eventMapper;
	private final ReadRepositoryPort<Bookshop> bookshopReadRepositoryAdapter;
	private final RequestByNameReadRepositoryPort requestByNameReadRepositoryBookshop;	
	
	public UpdateBookshopCommandHandler(EventWriteRepositoryPort eventWriteRepository, EventMapper eventMapper,
			ReadRepositoryPort<Bookshop> bookshopReadRepositoryAdapter,
			@Qualifier("bookshopReadRepositoryAdapter")RequestByNameReadRepositoryPort requestByNameReadRepositoryBookshop) {
		super();
		this.eventWriteRepository = eventWriteRepository;
		this.eventMapper = eventMapper;
		this.bookshopReadRepositoryAdapter = bookshopReadRepositoryAdapter;
		this.requestByNameReadRepositoryBookshop = requestByNameReadRepositoryBookshop;
	}	

	@Override
	public Mono<Event> handle(Command command) {
		
		return Mono.justOrEmpty(command)
				.ofType(UpdateBookshopCommand.class)
				.flatMap(this::validateUpdate)
				.map(this::createEvent)
				.flatMap(eventWriteRepository::save)
				.switchIfEmpty(Mono.error(new CreateEventException(Map.of(GlobalConstants.UPDATE_GENRE, ""))));	
	}

	private Mono<UpdateBookshopCommand> validateUpdate(UpdateBookshopCommand command) {
		
		return Mono.justOrEmpty(command)
				.flatMap(this::checkExistsBookshop)
				.flatMap(this::checkExistsByName)
				.thenReturn(command);
	}

	private Mono<UpdateBookshopCommand> checkExistsBookshop(UpdateBookshopCommand command) {

		return bookshopReadRepositoryAdapter.findById(command.bookshopId())
				.switchIfEmpty(Mono.error(new ResourceNotFoundException(
						Map.of(GlobalConstants.BS_ID, String.valueOf(command.bookshopId())))))
	    		.thenReturn(command);
	}

	private Mono<UpdateBookshopCommand> checkExistsByName(UpdateBookshopCommand command) {
		return requestByNameReadRepositoryBookshop.findByNameAndIdNot(command.name(), command.bookshopId())
	    		.filter(b -> !b)
	    		.switchIfEmpty(Mono.error(new UniqueValueException(
	    				Map.of(GlobalConstants.BS_NAME, command.name()))))
	    		.thenReturn(command);
	}

	private Event createEvent(Command command) {		
		return eventMapper.createEvent(GlobalConstants.BS_CAT, GlobalConstants.BS_UPDATED, command);
	}
}
