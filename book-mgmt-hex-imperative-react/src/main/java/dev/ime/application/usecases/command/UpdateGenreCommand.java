package dev.ime.application.usecases.command;

import dev.ime.domain.command.Command;

public record UpdateGenreCommand(Long genreId, String name, String description) implements Command {

}
