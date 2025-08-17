package dev.ime.application.usecases.command;

import dev.ime.domain.command.Command;

public record DeleteGenreCommand(Long genreId) implements Command {

}
