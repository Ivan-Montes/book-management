package dev.ime.application.utils;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.ime.application.exception.ValidationException;
import dev.ime.common.constants.GlobalConstants;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public final class MapExtractorHelper {

	private final ObjectMapper objectMapper;
	
	public UUID extractUuid(Map<String, Object> eventData, String key) {
		
	    return Optional.ofNullable(eventData.get(key))
	                   .map(Object::toString)
	                   .map(UUID::fromString)
	                   .orElseThrow(() -> new ValidationException(Map.of(GlobalConstants.OBJ_FIELD, key)));	    
	}

	public String extractAsString(Map<String, Object> eventData, String key, String patternConstraint) {
		
		String value = Optional.ofNullable(eventData.get(key))
	                   .map(Object::toString)
	                   .orElse("");
	    
	    Pattern compiledPattern = Pattern.compile(patternConstraint);
	    Matcher matcher = compiledPattern.matcher(value);
	    if (!matcher.matches()) {
	        throw new ValidationException(Map.of(GlobalConstants.OBJ_FIELD, key, GlobalConstants.OBJ_VALUE, value));
	    }

	    return value;	    
	}
	
	public Set<Long> extractRequiredLongSet(Map<String, Object> eventData, String key) {
	    return Optional.ofNullable(eventData.get(key))
	            .map(value -> objectMapper.convertValue(value, new TypeReference<Set<Long>>() {}))
	            .filter(set -> !set.isEmpty())
	            .orElseThrow(() -> new ValidationException(Map.of(GlobalConstants.OBJ_FIELD, key)));
	}
}
