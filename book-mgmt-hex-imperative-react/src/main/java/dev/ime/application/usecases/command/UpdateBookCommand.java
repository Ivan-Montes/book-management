package dev.ime.application.usecases.command;

import java.util.Set;

import dev.ime.domain.command.Command;

public record UpdateBookCommand(Long bookId, String isbn, String title, Long publisherId, Long genreId, Set<Long> authorIdSet) implements Command {

}
