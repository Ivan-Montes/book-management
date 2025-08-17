package dev.ime.application.usecases.command;

import dev.ime.domain.command.Command;

public record UpdatePublisherCommand(Long publisherId, String name) implements Command {

}
