package dev.ime.application.usecases.command;

import dev.ime.domain.command.Command;

public record DeleteAuthorCommand(Long authorId) implements Command  {

}
