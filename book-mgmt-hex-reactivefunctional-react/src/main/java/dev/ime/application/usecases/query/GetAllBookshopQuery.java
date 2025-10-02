package dev.ime.application.usecases.query;

import org.springframework.data.domain.Pageable;

import dev.ime.domain.query.Query;

public record GetAllBookshopQuery(Pageable pageable) implements Query {

}
