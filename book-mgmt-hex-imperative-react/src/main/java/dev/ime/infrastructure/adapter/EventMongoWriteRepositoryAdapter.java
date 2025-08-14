package dev.ime.infrastructure.adapter;

import java.util.Optional;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import dev.ime.common.mapper.EventMapper;
import dev.ime.domain.model.Event;
import dev.ime.domain.port.outbound.EventWriteRepositoryPort;

@Repository
public class EventMongoWriteRepositoryAdapter implements EventWriteRepositoryPort {

	private final MongoTemplate mongoTemplate;
	private final EventMapper eventMapper;
	
	public EventMongoWriteRepositoryAdapter(MongoTemplate mongoTemplate, EventMapper eventMapper) {
		super();
		this.mongoTemplate = mongoTemplate;
		this.eventMapper = eventMapper;
	}

	@Override
	public Optional<Event> save(Event event) {

		return Optional.ofNullable(eventMapper.fromEventDomainToEventMongo(event))
				.map(mongoTemplate::save)
				.map(eventMapper::fromEventMongoToEventDomain);	
	}
}
