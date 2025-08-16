package dev.ime.application.usecases.query;

import dev.ime.domain.query.Query;

public record GetByIdGenreQuery(Long id) implements Query {

}
