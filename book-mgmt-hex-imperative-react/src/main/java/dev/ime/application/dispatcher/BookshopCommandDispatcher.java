package dev.ime.application.dispatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import dev.ime.application.exception.IllegalHandlerException;
import dev.ime.application.handlers.command.CreateBookshopCommandHandler;
import dev.ime.application.handlers.command.DeleteBookshopCommandHandler;
import dev.ime.application.handlers.command.UpdateBookshopCommandHandler;
import dev.ime.application.usecases.command.CreateBookshopCommand;
import dev.ime.application.usecases.command.DeleteBookshopCommand;
import dev.ime.application.usecases.command.UpdateBookshopCommand;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.port.inbound.CommandDispatcher;

@Component
@Qualifier("bookshopCommandDispatcher")
public class BookshopCommandDispatcher implements CommandDispatcher {

	private final Map<Class<? extends Command>, CommandHandler> commandHandlers = new HashMap<>();

	public BookshopCommandDispatcher(CreateBookshopCommandHandler createCommandHandler,
			UpdateBookshopCommandHandler updateCommandHandler, DeleteBookshopCommandHandler deleteCommandHandler) {
		super();
		commandHandlers.put(CreateBookshopCommand.class, createCommandHandler);
		commandHandlers.put(UpdateBookshopCommand.class, updateCommandHandler);
		commandHandlers.put(DeleteBookshopCommand.class, deleteCommandHandler);
	}

	public CommandHandler getCommandHandler(Command command) {

		Optional<CommandHandler> optHandler = Optional.ofNullable( commandHandlers.get(command.getClass()) );
		
		return optHandler.orElseThrow( () -> new IllegalHandlerException(Map.of(GlobalConstants.OBJ_VALUE, command.getClass().getName())));	
		
	}
}
