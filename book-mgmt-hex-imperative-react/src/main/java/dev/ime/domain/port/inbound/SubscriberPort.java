package dev.ime.domain.port.inbound;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import dev.ime.domain.model.Event;

public interface SubscriberPort {
	
	void onMessage(ConsumerRecord<String, Event> consumerRecord);
}
