package dev.ime.application.usecases.command;

import dev.ime.domain.command.Command;

public record DeleteBookshopCommand(Long bookshopId) implements Command {

}
