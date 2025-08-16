package dev.ime.application.usecases.query;

import dev.ime.domain.query.Query;

public record GetByIdPublisherQuery(Long id) implements Query {

}
