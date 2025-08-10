package dev.ime.domain.command;

import java.util.Optional;

import dev.ime.domain.model.Event;

public interface CommandHandler {
	
	Optional<Event> handle(Command command);
}
