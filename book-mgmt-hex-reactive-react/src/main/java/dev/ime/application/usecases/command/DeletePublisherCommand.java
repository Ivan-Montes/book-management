package dev.ime.application.usecases.command;

import java.util.UUID;

import dev.ime.domain.command.Command;

public record DeletePublisherCommand(UUID publisherId) implements Command {

}
