package dev.ime.application.utils;

import java.util.Map;
import java.util.Optional;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;

import dev.ime.application.exception.ValidationException;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.domain.model.Event;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KafkaSubscriberValidationHelper {

	public void validateTopic(ConsumerRecord<String, Event> consumer, String originClass) {

		Optional.ofNullable(consumer.topic()).filter(topic -> !topic.trim().isEmpty())
				.orElseThrow(() -> new ValidationException(
						Map.of(originClass, GlobalConstants.EX_VALIDATION_DESC)));
	}

	public void validateValue(ConsumerRecord<String, Event> consumer, String originClass) {

		Optional.ofNullable(consumer.value()).orElseThrow(() -> new ValidationException(
				Map.of(originClass, GlobalConstants.EX_VALIDATION_DESC)));
	}
}
