package dev.ime.application.utils;

import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import dev.ime.application.dto.AuthorDto;
import dev.ime.application.dto.BookBookshopDto;
import dev.ime.application.dto.BookDto;
import dev.ime.application.dto.BookshopDto;
import dev.ime.application.dto.GenreDto;
import dev.ime.application.dto.PublisherDto;
import dev.ime.domain.model.Author;
import dev.ime.domain.model.Book;
import dev.ime.domain.model.BookBookshop;
import dev.ime.domain.model.Bookshop;
import dev.ime.domain.model.Genre;
import dev.ime.domain.model.Publisher;

@Component
public class SortingValidationUtils {

	private final Map<Class<?>, Set<String>> validSortFieldsMap;
	private final ReflectionUtils reflectionUtils;

	public SortingValidationUtils(ReflectionUtils reflectionUtils) {
		super();
		this.reflectionUtils = reflectionUtils;
		this.validSortFieldsMap = initializeMap();
	}

	private Map<Class<?>, Set<String>> initializeMap() {

		return Map.of(
				AuthorDto.class, reflectionUtils.getFieldNames(Author.class),
				BookBookshopDto.class, reflectionUtils.getFieldNames(BookBookshop.class),
				BookDto.class, reflectionUtils.getFieldNames(Book.class),
				BookshopDto.class, reflectionUtils.getFieldNames(Bookshop.class),
				GenreDto.class, reflectionUtils.getFieldNames(Genre.class),
				PublisherDto.class, reflectionUtils.getFieldNames(Publisher.class)
				);
	}

	private boolean isValidKeyclass(Class<?> keyClass) {

		return validSortFieldsMap.containsKey(keyClass);
	}

	public String getDefaultSortField(Class<?> keyClass) {

		Set<String> validFieldsSet = validSortFieldsMap.get(keyClass);

		if (validFieldsSet == null || validFieldsSet.isEmpty()) {
			return "";
		}

		String expectedIdField = getExpectedIdField(keyClass);

		return validFieldsSet.stream().filter(fieldName -> fieldName.toLowerCase().equals(expectedIdField)).findFirst()
				.orElse(validFieldsSet.iterator().next());
	}

	private String getExpectedIdField(Class<?> keyClass) {

		String sufixDto = keyClass.getSimpleName().toLowerCase();
		String sufixClass = sufixDto.substring(0, sufixDto.length() - 3);

		return sufixClass + "id";
	}

	public boolean isValidSortField(Class<?> keyClass, String sortField) {

		if (!isValidKeyclass(keyClass)) {
			return false;
		}

		return validSortFieldsMap.get(keyClass).stream().anyMatch(fieldName -> fieldName.equals(sortField));
	}
}
