package dev.ime.application.usecases.command;

import dev.ime.domain.command.Command;

public record DeletePublisherCommand(Long publisherId) implements Command {

}
