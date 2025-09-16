package dev.ime.infrastructure.adapter;

import java.util.Map;
import java.util.function.Function;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import dev.ime.application.exception.IllegalHandlerException;
import dev.ime.application.utils.KafkaSubscriberValidationHelper;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.domain.model.Event;
import dev.ime.domain.port.inbound.SubscriberPort;
import dev.ime.domain.port.outbound.ProjectorPort;
import reactor.core.publisher.Mono;

@Component
public class KafkaGenreSubscriberAdapter implements SubscriberPort {

	private final ProjectorPort projectorPort;
	private final Map<String, Function<Event, Mono<?>>> actionsMap;	
	private static final Logger logger = LoggerFactory.getLogger(KafkaGenreSubscriberAdapter.class);
	private final KafkaSubscriberValidationHelper kafkaSubscriberValidationHelper;
	
	public KafkaGenreSubscriberAdapter(@Qualifier("genreProjectorAdapter")ProjectorPort projectorPort,
			KafkaSubscriberValidationHelper kafkaSubscriberValidationHelper) {
		super();
		this.projectorPort = projectorPort;
		this.actionsMap = initializeActionsMap();
		this.kafkaSubscriberValidationHelper = kafkaSubscriberValidationHelper;
	}

	private Map<String, Function<Event, Mono<?>>> initializeActionsMap() {

		return Map.of(GlobalConstants.GENRE_CREATED, projectorPort::create, GlobalConstants.GENRE_UPDATED,
				projectorPort::update, GlobalConstants.GENRE_DELETED, projectorPort::deleteById);
	}

	@KafkaListener(topics = { GlobalConstants.GENRE_CREATED, GlobalConstants.GENRE_UPDATED, GlobalConstants.GENRE_DELETED },
			groupId = "bookmgmt-consumer-genre")
	@Override
	public Mono<Void> onMessage(ConsumerRecord<String, Event> consumerRecord) {

		return Mono.just(consumerRecord)
				.doOnNext( c -> logger.info(GlobalConstants.MSG_PATTERN_INFO, GlobalConstants.EVENT_CAT, GlobalConstants.MSG_SUBS_RECEIVED))
				.flatMap( c -> kafkaSubscriberValidationHelper.validateTopic(c, this.getClass().getSimpleName()))
				.flatMap( c -> kafkaSubscriberValidationHelper.validateValue(c, this.getClass().getSimpleName()))
				.doOnNext( c -> logger.info(GlobalConstants.MSG_PATTERN_INFO, consumerRecord.topic(), consumerRecord.value()))				
				.flatMap(this::processEvent)
				.onErrorResume( error ->  Mono.fromRunnable(()-> logger.error(GlobalConstants.MSG_PATTERN_SEVERE, GlobalConstants.EX_PLAIN, error.getMessage() != null? error.getMessage():GlobalConstants.EX_PLAIN)))
				.then();	
	}
	
    private Mono<Void> processEvent(ConsumerRecord<String, Event> consumer ){
    	
    	return Mono.justOrEmpty(consumer.topic())
    	.map(actionsMap::get)
		.switchIfEmpty(Mono.error(new IllegalHandlerException(Map.of(this.getClass().getSimpleName(), consumer.topic()))))
		.flatMap( function -> function.apply(consumer.value()))
    	.then();
    }
}
