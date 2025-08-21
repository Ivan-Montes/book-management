package dev.ime.infrastructure.adapter;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

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

@Component
public class KafkaBookSubscriberAdapter implements SubscriberPort {

	private final ProjectorPort projectorPort;
	private final Map<String, Consumer<Event>> actionsMap;
	private static final Logger logger = LoggerFactory.getLogger(KafkaBookSubscriberAdapter.class);
	private final KafkaSubscriberValidationHelper kafkaSubscriberValidationHelper;
	
	public KafkaBookSubscriberAdapter(@Qualifier("bookProjectorAdapter")ProjectorPort projectorPort,
			KafkaSubscriberValidationHelper kafkaSubscriberValidationHelper) {
		super();
		this.projectorPort = projectorPort;
		this.actionsMap = initializeActionsMap();
		this.kafkaSubscriberValidationHelper = kafkaSubscriberValidationHelper;
	}
	private Map<String, Consumer<Event>> initializeActionsMap() {

		return Map.of(GlobalConstants.BOOK_CREATED, projectorPort::create, GlobalConstants.BOOK_UPDATED,
				projectorPort::update, GlobalConstants.BOOK_DELETED, projectorPort::deleteById);
	}

	@KafkaListener(topics = { GlobalConstants.BOOK_CREATED, GlobalConstants.BOOK_UPDATED, GlobalConstants.BOOK_DELETED },
			groupId = "bookmgmt-consumer-book")
	@Override
	public void onMessage(ConsumerRecord<String, Event> consumerRecord) {

		logger.info(GlobalConstants.MSG_PATTERN_INFO, GlobalConstants.EVENT_CAT, GlobalConstants.MSG_SUBS_RECEIVED);
		kafkaSubscriberValidationHelper.validateTopic(consumerRecord, this.getClass().getSimpleName());
		kafkaSubscriberValidationHelper.validateValue(consumerRecord, this.getClass().getSimpleName());
		logger.info(GlobalConstants.MSG_PATTERN_INFO, consumerRecord.topic(), consumerRecord.value());
		processEvent(consumerRecord);
	}

	private void processEvent(ConsumerRecord<String, Event> consumer) {

		var handler = Optional.ofNullable(actionsMap.get(consumer.topic())).orElseThrow(
				() -> new IllegalHandlerException(Map.of(this.getClass().getSimpleName(), consumer.topic())));

		handler.accept(consumer.value());
	}
}
