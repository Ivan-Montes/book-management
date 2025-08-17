package dev.ime.application.usecases.command;

import dev.ime.domain.command.Command;

public record UpdateAuthorCommand(Long authorId, String name, String surname) implements Command  {

}
