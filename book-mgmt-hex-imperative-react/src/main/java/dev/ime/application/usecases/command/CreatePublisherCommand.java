package dev.ime.application.usecases.command;

import dev.ime.domain.command.Command;

public record CreatePublisherCommand(String name) implements Command {

}
