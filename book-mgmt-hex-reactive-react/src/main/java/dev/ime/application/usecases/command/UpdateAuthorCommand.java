package dev.ime.application.usecases.command;

import java.util.UUID;

import dev.ime.domain.command.Command;

public record UpdateAuthorCommand(UUID authorId, String name, String surname) implements Command  {

}
