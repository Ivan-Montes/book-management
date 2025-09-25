package dev.ime.domain.model;

import java.util.List;

public interface PaginatedResult<T> {
    List<T> content();
    int page();
    int size();
    long totalElements();
    int totalPages();
    boolean last();
}
