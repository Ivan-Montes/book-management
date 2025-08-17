package dev.ime.application.usecases.command;

import dev.ime.domain.command.Command;

public record UpdateBookshopCommand(Long bookshopId, String name) implements Command {

}
