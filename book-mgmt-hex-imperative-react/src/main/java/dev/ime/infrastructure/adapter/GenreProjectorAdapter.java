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
import dev.ime.infrastructure.entity.GenreJpaEntity;
import dev.ime.infrastructure.repository.GenreRepository;

@Repository
@Qualifier("genreProjectorAdapter")
public class GenreProjectorAdapter implements ProjectorPort {

	private final GenreRepository genreRepository;
	private final MapExtractorHelper mapExtractorHelper;
	private final PrintHelper printHelper;
	private static final Logger logger = LoggerFactory.getLogger(GenreProjectorAdapter.class);
	
	public GenreProjectorAdapter(GenreRepository genreRepository, MapExtractorHelper mapExtractorHelper,
			PrintHelper printHelper) {
		super();
		this.genreRepository = genreRepository;
		this.mapExtractorHelper = mapExtractorHelper;
		this.printHelper = printHelper;
	}

	@Override
	public void create(Event event) {

		GenreJpaEntity item = createJpaEntityForInsert(event.getEventData());
		GenreJpaEntity jpaEntitySaved = genreRepository.save(item);
		printHelper.printResult(event.getEventType(), jpaEntitySaved, logger);		
	}

	private GenreJpaEntity createJpaEntityForInsert(Map<String, Object> eventData) {

		String name = mapExtractorHelper.extractAsString(eventData, GlobalConstants.MODEL_NAME,
				GlobalConstants.PATTERN_NAME_FULL);
		String description = mapExtractorHelper.extractAsString(eventData, GlobalConstants.MODEL_DESC,
				GlobalConstants.PATTERN_DESC_FULL);

		return GenreJpaEntity.builder().name(name).description(description).build();
	}

	@Override
	public void update(Event event) {

		GenreJpaEntity item = createJpaEntityForUpdate(event.getEventData());
		Optional<GenreJpaEntity> opt = genreRepository.findById(item.getGenreId());
		if (opt.isEmpty()) {
			logger.error(GlobalConstants.MSG_PATTERN_SEVERE, event.getEventType(), item);
			return;
		}
		GenreJpaEntity jpaEntity = opt.get();
		jpaEntity.setName(item.getName());
		jpaEntity.setDescription(item.getDescription());

		GenreJpaEntity jpaEntitySaved = genreRepository.save(jpaEntity);
		printHelper.printResult(event.getEventType(), jpaEntitySaved, logger);
	}

	private GenreJpaEntity createJpaEntityForUpdate(Map<String, Object> eventData) {

		Long genreId = Long.valueOf(
				mapExtractorHelper.extractAsString(eventData, GlobalConstants.GENRE_ID, GlobalConstants.PATTERN_ID));
		String name = mapExtractorHelper.extractAsString(eventData, GlobalConstants.MODEL_NAME,
				GlobalConstants.PATTERN_NAME_FULL);
		String description = mapExtractorHelper.extractAsString(eventData, GlobalConstants.MODEL_DESC,
				GlobalConstants.PATTERN_DESC_FULL);

		return GenreJpaEntity.builder().genreId(genreId).name(name).description(description).build();
	}

	@Override
	public void deleteById(Event event) {

		Long genreId = Long.valueOf(
				mapExtractorHelper.extractAsString(event.getEventData(), GlobalConstants.GENRE_ID, GlobalConstants.PATTERN_ID));
		Optional<GenreJpaEntity> opt = genreRepository.findById(genreId);
		if (opt.isEmpty()) {
			logger.error(GlobalConstants.MSG_PATTERN_SEVERE, event.getEventType(), genreId);
			return;
		}
		genreRepository.deleteById(genreId);
		boolean boolResult = genreRepository.findById(genreId).isEmpty();
		printHelper.printResult(event.getEventType(), boolResult, logger);
	}
}
