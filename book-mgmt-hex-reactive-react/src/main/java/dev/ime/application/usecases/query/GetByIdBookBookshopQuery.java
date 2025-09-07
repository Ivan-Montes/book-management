package dev.ime.application.usecases.query;

import java.util.UUID;

import dev.ime.domain.query.Query;

public record GetByIdBookBookshopQuery(UUID bookId, UUID bookshopId) implements Query {

}
