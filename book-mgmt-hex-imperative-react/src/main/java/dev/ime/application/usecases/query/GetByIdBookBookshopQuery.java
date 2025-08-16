package dev.ime.application.usecases.query;

import dev.ime.domain.query.Query;

public record GetByIdBookBookshopQuery(Long bookId, Long bookshopId) implements Query {

}
