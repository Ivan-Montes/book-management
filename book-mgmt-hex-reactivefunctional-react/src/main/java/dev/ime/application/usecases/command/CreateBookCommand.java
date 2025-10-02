package dev.ime.application.usecases.command;

import java.util.Set;
import java.util.UUID;

import dev.ime.domain.command.Command;

public record CreateBookCommand(UUID bookId, String isbn, String title, UUID publisherId, UUID genreId, Set<UUID> authorIdSet) implements Command {

}
