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
import dev.ime.infrastructure.entity.BookshopJpaEntity;
import dev.ime.infrastructure.repository.BookshopRepository;

@Repository
@Qualifier("bookshopProjectorAdapter")
public class BookshopProjectorAdapter implements ProjectorPort {

	private final BookshopRepository bookshopRepository;
	private final MapExtractorHelper mapExtractorHelper;
	private final PrintHelper printHelper;
	private static final Logger logger = LoggerFactory.getLogger(BookshopProjectorAdapter.class);
	
	public BookshopProjectorAdapter(BookshopRepository bookshopRepository, MapExtractorHelper mapExtractorHelper,
			PrintHelper printHelper) {
		super();
		this.bookshopRepository = bookshopRepository;
		this.mapExtractorHelper = mapExtractorHelper;
		this.printHelper = printHelper;
	}

	@Override
	public void create(Event event) {

		BookshopJpaEntity item = createJpaEntityForInsert(event.getEventData());
		BookshopJpaEntity jpaEntitySaved = bookshopRepository.save(item);
		printHelper.printResult(event.getEventType(), jpaEntitySaved, logger);		
	}

	private BookshopJpaEntity createJpaEntityForInsert(Map<String, Object> eventData) {

		String name = mapExtractorHelper.extractAsString(eventData, GlobalConstants.MODEL_NAME,
				GlobalConstants.PATTERN_NAME_FULL);

		return BookshopJpaEntity.builder().name(name).build();
	}

	@Override
	public void update(Event event) {

		BookshopJpaEntity item = createJpaEntityForUpdate(event.getEventData());
		Optional<BookshopJpaEntity> opt = bookshopRepository.findById(item.getBookshopId());
		if (opt.isEmpty()) {
			logger.error(GlobalConstants.MSG_PATTERN_SEVERE, event.getEventType(), item);
			return;
		}
		BookshopJpaEntity jpaEntity = opt.get();
		jpaEntity.setName(item.getName());

		BookshopJpaEntity jpaEntitySaved = bookshopRepository.save(jpaEntity);
		printHelper.printResult(event.getEventType(), jpaEntitySaved, logger);
	}

	private BookshopJpaEntity createJpaEntityForUpdate(Map<String, Object> eventData) {

		Long bookshopId = Long.valueOf(
				mapExtractorHelper.extractAsString(eventData, GlobalConstants.BS_ID, GlobalConstants.PATTERN_ID));
		String name = mapExtractorHelper.extractAsString(eventData, GlobalConstants.MODEL_NAME,
				GlobalConstants.PATTERN_NAME_FULL);

		return BookshopJpaEntity.builder().bookshopId(bookshopId).name(name).build();
	}

	@Override
	public void deleteById(Event event) {

		Long bookshopId = Long.valueOf(
				mapExtractorHelper.extractAsString(event.getEventData(), GlobalConstants.BS_ID, GlobalConstants.PATTERN_ID));
		Optional<BookshopJpaEntity> opt = bookshopRepository.findById(bookshopId);
		if (opt.isEmpty()) {
			logger.error(GlobalConstants.MSG_PATTERN_SEVERE, event.getEventType(), bookshopId);
			return;
		}
		bookshopRepository.deleteById(bookshopId);
		boolean boolResult = bookshopRepository.findById(bookshopId).isEmpty();
		printHelper.printResult(event.getEventType(), boolResult, logger);
	}
}
