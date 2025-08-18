package dev.ime.application.dispatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import dev.ime.application.exception.IllegalHandlerException;
import dev.ime.application.handlers.command.CreateAuthorCommandHandler;
import dev.ime.application.handlers.command.DeleteAuthorCommandHandler;
import dev.ime.application.handlers.command.UpdateAuthorCommandHandler;
import dev.ime.application.usecases.command.CreateAuthorCommand;
import dev.ime.application.usecases.command.DeleteAuthorCommand;
import dev.ime.application.usecases.command.UpdateAuthorCommand;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.port.inbound.CommandDispatcher;

@Component
@Qualifier("authorCommandDispatcher")
public class AuthorCommandDispatcher implements CommandDispatcher {

	private final Map<Class<? extends Command>, CommandHandler> commandHandlers = new HashMap<>();

	public AuthorCommandDispatcher(CreateAuthorCommandHandler createCommandHandler,
			UpdateAuthorCommandHandler updateCommandHandler, DeleteAuthorCommandHandler deleteCommandHandler) {
		super();
		commandHandlers.put(CreateAuthorCommand.class, createCommandHandler);
		commandHandlers.put(UpdateAuthorCommand.class, updateCommandHandler);
		commandHandlers.put(DeleteAuthorCommand.class, deleteCommandHandler);
	}

	public CommandHandler getCommandHandler(Command command) {

		Optional<CommandHandler> optHandler = Optional.ofNullable( commandHandlers.get(command.getClass()) );
		
		return optHandler.orElseThrow( () -> new IllegalHandlerException(Map.of(GlobalConstants.OBJ_VALUE, command.getClass().getName())));	
		
	}	
}
