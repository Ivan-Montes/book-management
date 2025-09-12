package dev.ime.application.utils;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;

import dev.ime.application.exception.ValidationException;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.domain.model.Event;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class KafkaSubscriberValidationHelper {

	public Mono<ConsumerRecord<String, Event>> validateTopic(ConsumerRecord<String, Event> consumer, String originClass){
		
		return Mono.justOrEmpty(consumer.topic())
				.filter( topic -> !topic.trim().isEmpty() )
				.switchIfEmpty(Mono.error(new ValidationException(Map.of(originClass, GlobalConstants.EX_VALIDATION_DESC))))
				.thenReturn(consumer);
	}
	
	public Mono<ConsumerRecord<String, Event>> validateValue(ConsumerRecord<String, Event> consumer, String originClass){
		
		return Mono.justOrEmpty(consumer.value())
				.switchIfEmpty(Mono.error(new ValidationException(Map.of(originClass, GlobalConstants.EX_VALIDATION_DESC))))
				.thenReturn(consumer);
	}
}
