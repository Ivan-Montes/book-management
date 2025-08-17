package dev.ime.application.usecases.command;

import dev.ime.domain.command.Command;

public record DeleteBookCommand(Long bookId) implements Command {

}
