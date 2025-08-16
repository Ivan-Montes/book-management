package dev.ime.application.usecases.query;

import dev.ime.domain.query.Query;

public record GetByIdBookQuery(Long id) implements Query {

}
