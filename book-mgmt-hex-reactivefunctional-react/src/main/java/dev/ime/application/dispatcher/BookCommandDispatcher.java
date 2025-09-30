package dev.ime.application.dispatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import dev.ime.application.exception.IllegalHandlerException;
import dev.ime.application.handlers.command.CreateBookCommandHandler;
import dev.ime.application.handlers.command.DeleteBookCommandHandler;
import dev.ime.application.handlers.command.UpdateBookCommandHandler;
import dev.ime.application.usecases.command.CreateBookCommand;
import dev.ime.application.usecases.command.DeleteBookCommand;
import dev.ime.application.usecases.command.UpdateBookCommand;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.port.inbound.CommandDispatcher;

@Component
@Qualifier("bookCommandDispatcher")
public class BookCommandDispatcher implements CommandDispatcher {

	private final Map<Class<? extends Command>, CommandHandler> commandHandlers = new HashMap<>();

	public BookCommandDispatcher(CreateBookCommandHandler createCommandHandler,
			UpdateBookCommandHandler updateCommandHandler, DeleteBookCommandHandler deleteCommandHandler) {
		super();
		commandHandlers.put(CreateBookCommand.class, createCommandHandler);
		commandHandlers.put(UpdateBookCommand.class, updateCommandHandler);
		commandHandlers.put(DeleteBookCommand.class, deleteCommandHandler);
	}

	public CommandHandler getCommandHandler(Command command) {

		Optional<CommandHandler> optHandler = Optional.ofNullable( commandHandlers.get(command.getClass()) );
		
		return optHandler.orElseThrow( () -> new IllegalHandlerException(Map.of(GlobalConstants.OBJ_VALUE, command.getClass().getName())));	
		
	}	
}
