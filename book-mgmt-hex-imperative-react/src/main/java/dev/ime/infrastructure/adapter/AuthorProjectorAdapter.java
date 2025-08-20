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
import dev.ime.infrastructure.entity.AuthorJpaEntity;
import dev.ime.infrastructure.repository.AuthorRepository;

@Repository
@Qualifier("authorProjectorAdapter")
public class AuthorProjectorAdapter implements ProjectorPort {

	private final AuthorRepository authorRepository;
	private final MapExtractorHelper mapExtractorHelper;
	private final PrintHelper printHelper;
	private static final Logger logger = LoggerFactory.getLogger(AuthorProjectorAdapter.class);

	public AuthorProjectorAdapter(AuthorRepository authorRepository, MapExtractorHelper mapExtractorHelper,
			PrintHelper printHelper) {
		super();
		this.authorRepository = authorRepository;
		this.mapExtractorHelper = mapExtractorHelper;
		this.printHelper = printHelper;
	}

	@Override
	public void create(Event event) {

		AuthorJpaEntity item = createJpaEntityForInsert(event.getEventData());
		AuthorJpaEntity jpaEntitySaved = authorRepository.save(item);
		printHelper.printResult(event.getEventType(), jpaEntitySaved, logger);
	}

	private AuthorJpaEntity createJpaEntityForInsert(Map<String, Object> eventData) {

		String name = mapExtractorHelper.extractAsString(eventData, GlobalConstants.MODEL_NAME,
				GlobalConstants.PATTERN_NAME_FULL);
		String surname = mapExtractorHelper.extractAsString(eventData, GlobalConstants.MODEL_SURNAME,
				GlobalConstants.PATTERN_SURNAME_FULL);

		return AuthorJpaEntity.builder().name(name).surname(surname).build();
	}

	@Override
	public void update(Event event) {

		AuthorJpaEntity item = createJpaEntityForUpdate(event.getEventData());
		Optional<AuthorJpaEntity> opt = authorRepository.findById(item.getAuthorId());
		if (opt.isEmpty()) {
			logger.error(GlobalConstants.MSG_PATTERN_SEVERE, event.getEventType(), item);
			return;
		}
		AuthorJpaEntity jpaEntity = opt.get();
		jpaEntity.setName(item.getName());
		jpaEntity.setSurname(item.getSurname());

		AuthorJpaEntity jpaEntitySaved = authorRepository.save(jpaEntity);
		printHelper.printResult(event.getEventType(), jpaEntitySaved, logger);
	}

	private AuthorJpaEntity createJpaEntityForUpdate(Map<String, Object> eventData) {

		Long authorId = Long.valueOf(
				mapExtractorHelper.extractAsString(eventData, GlobalConstants.AUTHOR_ID, GlobalConstants.PATTERN_ID));
		String name = mapExtractorHelper.extractAsString(eventData, GlobalConstants.MODEL_NAME,
				GlobalConstants.PATTERN_NAME_FULL);
		String surname = mapExtractorHelper.extractAsString(eventData, GlobalConstants.MODEL_SURNAME,
				GlobalConstants.PATTERN_SURNAME_FULL);

		return AuthorJpaEntity.builder().authorId(authorId).name(name).surname(surname).build();
	}

	@Override
	public void deleteById(Event event) {

		Long authorId = Long.valueOf(mapExtractorHelper.extractAsString(event.getEventData(), GlobalConstants.AUTHOR_ID,
				GlobalConstants.PATTERN_ID));
		Optional<AuthorJpaEntity> opt = authorRepository.findById(authorId);
		if (opt.isEmpty()) {
			logger.error(GlobalConstants.MSG_PATTERN_SEVERE, event.getEventType(), authorId);
			return;
		}
		authorRepository.deleteById(authorId);
		boolean boolResult = authorRepository.findById(authorId).isEmpty();
		printHelper.printResult(event.getEventType(), boolResult, logger);
	}
}
