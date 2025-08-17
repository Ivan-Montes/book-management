package dev.ime.application.usecases.command;

import dev.ime.domain.command.Command;

public record UpdateBookBookshopCommand(Long bookId, Long bookshopId, Double price,	Integer units) implements Command  {

}
