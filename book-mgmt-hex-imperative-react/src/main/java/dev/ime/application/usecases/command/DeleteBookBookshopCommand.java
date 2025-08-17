package dev.ime.application.usecases.command;

import dev.ime.domain.command.Command;

public record DeleteBookBookshopCommand(Long bookId, Long bookshopId) implements Command  {

}
