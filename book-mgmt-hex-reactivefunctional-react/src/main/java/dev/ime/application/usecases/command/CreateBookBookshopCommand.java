package dev.ime.application.usecases.command;

import java.util.UUID;

import dev.ime.domain.command.Command;

public record CreateBookBookshopCommand(UUID bookId, UUID bookshopId, Double price,	Integer units) implements Command  {

}
