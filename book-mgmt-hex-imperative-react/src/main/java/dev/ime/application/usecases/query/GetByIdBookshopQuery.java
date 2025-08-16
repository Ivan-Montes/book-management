package dev.ime.application.usecases.query;

import dev.ime.domain.query.Query;

public record GetByIdBookshopQuery(Long id) implements Query {

}
