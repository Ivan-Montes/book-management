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
import dev.ime.infrastructure.entity.BookBookshopId;
import dev.ime.infrastructure.entity.BookBookshopJpaEntity;
import dev.ime.infrastructure.entity.BookJpaEntity;
import dev.ime.infrastructure.entity.BookshopJpaEntity;
import dev.ime.infrastructure.repository.BookBookshopRepository;

@Repository
@Qualifier("bookBookshopProjectorAdapter")
public class BookBookshopProjectorAdapter implements ProjectorPort {

	private final BookBookshopRepository bookBookshopRepositoryAdapter;
	private final MapExtractorHelper mapExtractorHelper;
	private final PrintHelper printHelper;
	private static final Logger logger = LoggerFactory.getLogger(BookBookshopProjectorAdapter.class);

	public BookBookshopProjectorAdapter(BookBookshopRepository bookBookshopRepositoryAdapter,
			MapExtractorHelper mapExtractorHelper, PrintHelper printHelper) {
		super();
		this.bookBookshopRepositoryAdapter = bookBookshopRepositoryAdapter;
		this.mapExtractorHelper = mapExtractorHelper;
		this.printHelper = printHelper;
	}

	@Override
	public void create(Event event) {

		BookBookshopJpaEntity item = createJpaEntity(event.getEventData());
		BookBookshopJpaEntity jpaEntitySaved = bookBookshopRepositoryAdapter.save(item);
		printHelper.printResult(event.getEventType(), jpaEntitySaved, logger);
	}

	private BookBookshopJpaEntity createJpaEntity(Map<String, Object> eventData) {

		Long bookId = Long.valueOf(
				mapExtractorHelper.extractAsString(eventData, GlobalConstants.BOOK_ID, GlobalConstants.PATTERN_ID));
		Long bookshopId = Long.valueOf(
				mapExtractorHelper.extractAsString(eventData, GlobalConstants.BS_ID, GlobalConstants.PATTERN_ID));
		Double price = Double.valueOf(mapExtractorHelper.extractAsString(eventData, GlobalConstants.BBS_PRICE,
				GlobalConstants.PATTERN_PRICE));
		Integer units = Integer.valueOf(mapExtractorHelper.extractAsString(eventData, GlobalConstants.BBS_UNITS,
				GlobalConstants.PATTERN_UNITS));
		
		BookJpaEntity book = new BookJpaEntity();
		book.setBookId(bookId);
		
		BookshopJpaEntity bookshop = new BookshopJpaEntity();
		bookshop.setBookshopId(bookshopId);
		
		BookBookshopId bookBookshopId = new BookBookshopId(bookId, bookshopId);
		
		return BookBookshopJpaEntity.builder().bookBookshopId(bookBookshopId).book(book).bookshop(bookshop).price(price).units(units).build();
	}

	@Override
	public void update(Event event) {

		BookBookshopJpaEntity item = createJpaEntity(event.getEventData());
		Optional<BookBookshopJpaEntity> opt = bookBookshopRepositoryAdapter.findById(item.getBookBookshopId());
		if (opt.isEmpty()) {
			logger.error(GlobalConstants.MSG_PATTERN_SEVERE, event.getEventType(), item);
			return;
		}
		BookBookshopJpaEntity jpaEntity = opt.get();
		jpaEntity.setPrice(item.getPrice());
		jpaEntity.setUnits(item.getUnits());

		BookBookshopJpaEntity jpaEntitySaved = bookBookshopRepositoryAdapter.save(jpaEntity);
		printHelper.printResult(event.getEventType(), jpaEntitySaved, logger);
	}

	@Override
	public void deleteById(Event event) {

		Long bookId = Long.valueOf(
				mapExtractorHelper.extractAsString(event.getEventData(), GlobalConstants.BOOK_ID, GlobalConstants.PATTERN_ID));
		Long bookshopId = Long.valueOf(
				mapExtractorHelper.extractAsString(event.getEventData(), GlobalConstants.BS_ID, GlobalConstants.PATTERN_ID));
		BookBookshopId bookBookshopId = new BookBookshopId(bookId, bookshopId);
		Optional<BookBookshopJpaEntity> opt = bookBookshopRepositoryAdapter.findById(bookBookshopId);
		if (opt.isEmpty()) {
			logger.error(GlobalConstants.MSG_PATTERN_SEVERE, event.getEventType(), bookBookshopId);
			return;
		}
		bookBookshopRepositoryAdapter.deleteById(bookBookshopId);
		boolean boolResult = bookBookshopRepositoryAdapter.findById(bookBookshopId).isEmpty();
		printHelper.printResult(event.getEventType(), boolResult, logger);
	}
}
