package dev.ime.application.usecases.command;

import dev.ime.domain.command.Command;

public record CreateAuthorCommand(String name, String surname) implements Command  {

}
