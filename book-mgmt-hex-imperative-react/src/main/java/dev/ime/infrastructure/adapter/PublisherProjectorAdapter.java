package dev.ime.infrastructure.adapter;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import dev.ime.application.utils.MapExtractorHelper;
import dev.ime.application.utils.PrintHelper;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.domain.model.Event;
import dev.ime.domain.port.outbound.ProjectorPort;
import dev.ime.infrastructure.entity.PublisherJpaEntity;
import dev.ime.infrastructure.repository.PublisherRepository;

@Repository
@Qualifier("publisherProjectorAdapter")
public class PublisherProjectorAdapter implements ProjectorPort {

	private final PublisherRepository publisherRepository;
	private final MapExtractorHelper mapExtractorHelper;
	private final PrintHelper printHelper;
	private static final Logger logger = LoggerFactory.getLogger(PublisherProjectorAdapter.class);
	
	public PublisherProjectorAdapter(PublisherRepository publisherRepository, MapExtractorHelper mapExtractorHelper,
			PrintHelper printHelper) {
		super();
		this.publisherRepository = publisherRepository;
		this.mapExtractorHelper = mapExtractorHelper;
		this.printHelper = printHelper;
	}

	@Override
	public void create(Event event) {

		PublisherJpaEntity item = createJpaEntityForInsert(event.getEventData());
		PublisherJpaEntity jpaEntitySaved = publisherRepository.save(item);
		printHelper.printResult(event.getEventType(), jpaEntitySaved, logger);		
	}

	private PublisherJpaEntity createJpaEntityForInsert(Map<String, Object> eventData) {

		String name = mapExtractorHelper.extractAsString(eventData, GlobalConstants.MODEL_NAME,
				GlobalConstants.PATTERN_NAME_FULL);

		return PublisherJpaEntity.builder().name(name).build();
	}

	@Override
	public void update(Event event) {

		PublisherJpaEntity item = createJpaEntityForUpdate(event.getEventData());
		Optional<PublisherJpaEntity> opt = publisherRepository.findById(item.getPublisherId());
		if (opt.isEmpty()) {
			logger.error(GlobalConstants.MSG_PATTERN_SEVERE, event.getEventType(), item);
			return;
		}
		PublisherJpaEntity jpaEntity = opt.get();
		jpaEntity.setName(item.getName());

		PublisherJpaEntity jpaEntitySaved = publisherRepository.save(jpaEntity);
		printHelper.printResult(event.getEventType(), jpaEntitySaved, logger);
	}

	private PublisherJpaEntity createJpaEntityForUpdate(Map<String, Object> eventData) {

		Long publisherId = Long.valueOf(
				mapExtractorHelper.extractAsString(eventData, GlobalConstants.PUBLI_ID, GlobalConstants.PATTERN_ID));
		String name = mapExtractorHelper.extractAsString(eventData, GlobalConstants.MODEL_NAME,
				GlobalConstants.PATTERN_NAME_FULL);

		return PublisherJpaEntity.builder().publisherId(publisherId).name(name).build();
	}

	@Override
	public void deleteById(Event event) {

		Long publisherId = Long.valueOf(
				mapExtractorHelper.extractAsString(event.getEventData(), GlobalConstants.PUBLI_ID, GlobalConstants.PATTERN_ID));
		Optional<PublisherJpaEntity> opt = publisherRepository.findById(publisherId);
		if (opt.isEmpty()) {
			logger.error(GlobalConstants.MSG_PATTERN_SEVERE, event.getEventType(), publisherId);
			return;
		}
		publisherRepository.deleteById(publisherId);
		boolean boolResult = publisherRepository.findById(publisherId).isEmpty();
		printHelper.printResult(event.getEventType(), boolResult, logger);
	}
}
