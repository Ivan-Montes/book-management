package dev.ime.infrastructure.adapter;

import java.util.Optional;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import dev.ime.common.constants.GlobalConstants;
import dev.ime.domain.model.Event;
import dev.ime.domain.port.outbound.PublisherPort;

@Service
public class KafkaPublisherAdapter implements PublisherPort {

	private final KafkaTemplate<String, Object> kafkaTemplate;
	private static final Logger logger = LoggerFactory.getLogger(KafkaPublisherAdapter.class);

	public KafkaPublisherAdapter(KafkaTemplate<String, Object> kafkaTemplate) {
		super();
		this.kafkaTemplate = kafkaTemplate;
	}

	@Override
	public void publishEvent(Event event) {
		ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(event.getEventType(), event);
		kafkaTemplate.send(producerRecord).whenComplete((result, ex) -> {
			if (ex == null) {
				handleSuccess(result);
			} else {
				handleFailure(result, ex);
			}
		});
	}

	private void handleSuccess(SendResult<String, Object> result) {
		logger.info(GlobalConstants.MSG_PATTERN_INFO, GlobalConstants.MSG_PUBLISH_OK, createSendResultMsg(result));
	}

	private String createSendResultMsg(SendResult<String, Object> result) {
		return Optional.ofNullable(result).map(SendResult::getProducerRecord).map(pr -> String.format("%s]:[%s", pr.topic(), pr.value()))
				.orElse(GlobalConstants.MSG_NODATA);
	}

	private String createThrowableMsg(Throwable ex) {
		return Optional.ofNullable(ex).map(Throwable::getLocalizedMessage).orElse(GlobalConstants.MSG_NODATA);
	}

	private void handleFailure(SendResult<String, Object> result, Throwable ex) {
		logger.error(GlobalConstants.MSG_PATTERN_INFO, GlobalConstants.MSG_PUBLISH_FAIL,
				String.format("%s]:[%s", createSendResultMsg(result), createThrowableMsg(ex)));		
	}
}
