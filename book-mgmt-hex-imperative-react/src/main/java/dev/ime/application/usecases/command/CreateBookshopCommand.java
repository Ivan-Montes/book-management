package dev.ime.application.usecases.command;

import dev.ime.domain.command.Command;

public record CreateBookshopCommand(String name) implements Command {

}
