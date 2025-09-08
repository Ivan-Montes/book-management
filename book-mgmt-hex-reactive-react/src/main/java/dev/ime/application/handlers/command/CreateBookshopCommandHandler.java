package dev.ime.application.handlers.command;

import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import dev.ime.application.exception.CreateEventException;
import dev.ime.application.exception.UniqueValueException;
import dev.ime.application.usecases.command.CreateBookshopCommand;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.common.mapper.EventMapper;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.model.Event;
import dev.ime.domain.port.outbound.EventWriteRepositoryPort;
import dev.ime.domain.port.outbound.RequestByNameReadRepositoryPort;
import reactor.core.publisher.Mono;

@Component
public class CreateBookshopCommandHandler implements CommandHandler {

	private final EventWriteRepositoryPort eventWriteRepository;
	private final EventMapper eventMapper;
	private final RequestByNameReadRepositoryPort requestByNameReadRepositoryBookshop;
	
	public CreateBookshopCommandHandler(EventWriteRepositoryPort eventWriteRepository, EventMapper eventMapper,
			@Qualifier("bookshopReadRepositoryAdapter")RequestByNameReadRepositoryPort requestByNameReadRepositoryBookshop) {
		super();
		this.eventWriteRepository = eventWriteRepository;
		this.eventMapper = eventMapper;
		this.requestByNameReadRepositoryBookshop = requestByNameReadRepositoryBookshop;
	}

	@Override
	public Mono<Event> handle(Command command) {

		return Mono.justOrEmpty(command)
				.ofType(CreateBookshopCommand.class)
				.flatMap(this::validateCreate)
				.map(this::createEvent)
				.flatMap(eventWriteRepository::save)
				.switchIfEmpty(Mono.error(new CreateEventException(Map.of(GlobalConstants.CREATE_BS, ""))));	
	}

	private Mono<CreateBookshopCommand> validateCreate(CreateBookshopCommand command) {
		
		return Mono.justOrEmpty(command)
				.flatMap(this::checkExistsByName)
				.thenReturn(command);
	}

	private Mono<CreateBookshopCommand> checkExistsByName(CreateBookshopCommand command) {
	    return requestByNameReadRepositoryBookshop.existsByName(command.name())
	    		.filter(b -> !b)
	    		.switchIfEmpty(Mono.error(new UniqueValueException(Map.of(GlobalConstants.BS_NAME, command.name()))))
	    		.thenReturn(command);
	}
	
	private Event createEvent(Command command) {		
		return eventMapper.createEvent(GlobalConstants.BS_CAT, GlobalConstants.BS_CREATED, command);
	}
}
