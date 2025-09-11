package dev.ime.application.dispatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import dev.ime.application.exception.IllegalHandlerException;
import dev.ime.application.handlers.command.CreatePublisherCommandHandler;
import dev.ime.application.handlers.command.DeletePublisherCommandHandler;
import dev.ime.application.handlers.command.UpdatePublisherCommandHandler;
import dev.ime.application.usecases.command.CreatePublisherCommand;
import dev.ime.application.usecases.command.DeletePublisherCommand;
import dev.ime.application.usecases.command.UpdatePublisherCommand;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.port.inbound.CommandDispatcher;

@Component
@Qualifier("publisherCommandDispatcher")
public class PublisherCommandDispatcher implements CommandDispatcher {

	private final Map<Class<? extends Command>, CommandHandler> commandHandlers = new HashMap<>();

	public PublisherCommandDispatcher(CreatePublisherCommandHandler createCommandHandler,
			UpdatePublisherCommandHandler updateCommandHandler, DeletePublisherCommandHandler deleteCommandHandler) {
		super();
		commandHandlers.put(CreatePublisherCommand.class, createCommandHandler);
		commandHandlers.put(UpdatePublisherCommand.class, updateCommandHandler);
		commandHandlers.put(DeletePublisherCommand.class, deleteCommandHandler);
	}

	public CommandHandler getCommandHandler(Command command) {

		Optional<CommandHandler> optHandler = Optional.ofNullable( commandHandlers.get(command.getClass()) );
		
		return optHandler.orElseThrow( () -> new IllegalHandlerException(Map.of(GlobalConstants.OBJ_VALUE, command.getClass().getName())));	
		
	}	
}
