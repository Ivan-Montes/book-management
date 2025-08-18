package dev.ime.application.dispatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import dev.ime.application.exception.IllegalHandlerException;
import dev.ime.application.handlers.command.CreateGenreCommandHandler;
import dev.ime.application.handlers.command.DeleteGenreCommandHandler;
import dev.ime.application.handlers.command.UpdateGenreCommandHandler;
import dev.ime.application.usecases.command.CreateGenreCommand;
import dev.ime.application.usecases.command.DeleteGenreCommand;
import dev.ime.application.usecases.command.UpdateGenreCommand;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.port.inbound.CommandDispatcher;

@Component
@Qualifier("genreCommandDispatcher")
public class GenreCommandDispatcher implements CommandDispatcher {

	private final Map<Class<? extends Command>, CommandHandler> commandHandlers = new HashMap<>();

	public GenreCommandDispatcher(CreateGenreCommandHandler createCommandHandler,
			UpdateGenreCommandHandler updateCommandHandler, DeleteGenreCommandHandler deleteCommandHandler) {
		super();
		commandHandlers.put(CreateGenreCommand.class, createCommandHandler);
		commandHandlers.put(UpdateGenreCommand.class, updateCommandHandler);
		commandHandlers.put(DeleteGenreCommand.class, deleteCommandHandler);
	}

	public CommandHandler getCommandHandler(Command command) {

		Optional<CommandHandler> optHandler = Optional.ofNullable( commandHandlers.get(command.getClass()) );
		
		return optHandler.orElseThrow( () -> new IllegalHandlerException(Map.of(GlobalConstants.OBJ_VALUE, command.getClass().getName())));	
		
	}	
}
