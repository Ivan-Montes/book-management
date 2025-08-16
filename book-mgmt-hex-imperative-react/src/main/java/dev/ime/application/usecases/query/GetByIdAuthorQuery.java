package dev.ime.application.usecases.query;

import dev.ime.domain.query.Query;

public record GetByIdAuthorQuery(Long id) implements Query {

}
