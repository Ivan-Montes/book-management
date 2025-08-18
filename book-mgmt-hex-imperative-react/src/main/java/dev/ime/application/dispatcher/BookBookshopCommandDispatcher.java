package dev.ime.application.dispatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import dev.ime.application.exception.IllegalHandlerException;
import dev.ime.application.handlers.command.CreateBookBookshopCommandHandler;
import dev.ime.application.handlers.command.DeleteBookBookshopCommandHandler;
import dev.ime.application.handlers.command.UpdateBookBookshopCommandHandler;
import dev.ime.application.usecases.command.CreateBookBookshopCommand;
import dev.ime.application.usecases.command.DeleteBookBookshopCommand;
import dev.ime.application.usecases.command.UpdateBookBookshopCommand;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.port.inbound.CommandDispatcher;

@Component
@Qualifier("bookBookshopCommandDispatcher")
public class BookBookshopCommandDispatcher implements CommandDispatcher {

	private final Map<Class<? extends Command>, CommandHandler> commandHandlers = new HashMap<>();

	public BookBookshopCommandDispatcher(CreateBookBookshopCommandHandler createCommandHandler,
			UpdateBookBookshopCommandHandler updateCommandHandler, DeleteBookBookshopCommandHandler deleteCommandHandler) {
		super();
		commandHandlers.put(CreateBookBookshopCommand.class, createCommandHandler);
		commandHandlers.put(UpdateBookBookshopCommand.class, updateCommandHandler);
		commandHandlers.put(DeleteBookBookshopCommand.class, deleteCommandHandler);
	}

	public CommandHandler getCommandHandler(Command command) {

		Optional<CommandHandler> optHandler = Optional.ofNullable( commandHandlers.get(command.getClass()) );
		
		return optHandler.orElseThrow( () -> new IllegalHandlerException(Map.of(GlobalConstants.OBJ_VALUE, command.getClass().getName())));	
		
	}	
}
