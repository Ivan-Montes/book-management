package dev.ime.application.usecases.command;

import dev.ime.domain.command.Command;

public record CreateGenreCommand(String name, String description) implements Command {

}
