package dev.ime.application.usecases.command;

import dev.ime.domain.command.Command;

public record CreateBookBookshopCommand(Long bookId, Long bookshopId, Double price,	Integer units) implements Command  {

}
