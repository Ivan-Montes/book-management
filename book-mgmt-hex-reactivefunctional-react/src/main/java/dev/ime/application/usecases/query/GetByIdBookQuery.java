package dev.ime.application.usecases.query;

import java.util.UUID;

import dev.ime.domain.query.Query;

public record GetByIdBookQuery(UUID id) implements Query {

}
