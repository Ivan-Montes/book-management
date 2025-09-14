package dev.ime.infrastructure.adapter;

import java.util.Map;
import java.util.UUID;

import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import dev.ime.application.exception.CreateJpaEntityException;
import dev.ime.application.exception.EmptyResponseException;
import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.utils.LogHelper;
import dev.ime.application.utils.MapExtractorHelper;
import dev.ime.application.utils.PrintHelper;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.domain.model.Event;
import dev.ime.domain.port.outbound.ProjectorPort;
import dev.ime.infrastructure.entity.BookBookshopId;
import dev.ime.infrastructure.entity.BookBookshopJpaEntity;
import dev.ime.infrastructure.entity.BookJpaEntity;
import dev.ime.infrastructure.entity.BookshopJpaEntity;
import io.smallrye.mutiny.converters.uni.UniReactorConverters;
import reactor.core.publisher.Mono;

@Repository
@Qualifier("bookBookshopProjectorAdapter")
public class BookBookshopProjectorAdapter implements ProjectorPort {

	private final SessionFactory sessionFactory;
	private final MapExtractorHelper mapExtractorHelper;
	private final PrintHelper printHelper;
	private final LogHelper logHelper;
	private static final Logger logger = LoggerFactory.getLogger(BookBookshopProjectorAdapter.class);

	public BookBookshopProjectorAdapter(SessionFactory sessionFactory, MapExtractorHelper mapExtractorHelper,
			PrintHelper printHelper, LogHelper logHelper) {
		super();
		this.sessionFactory = sessionFactory;
		this.mapExtractorHelper = mapExtractorHelper;
		this.printHelper = printHelper;
		this.logHelper = logHelper;
	}

	@Override
	public Mono<Void> create(Event event) {

		return Mono.justOrEmpty(event.getEventData())	
				.doOnNext(data -> logHelper.logFlowStep(data, printHelper, logger))
				.flatMap(this::createJpaEntity)
				.doOnNext(data -> logHelper.logFlowStep(data, printHelper, logger))
				.flatMap(this::insertAction)
				.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(GlobalConstants.BBS_CAT, GlobalConstants.EX_EMPTYRESPONSE_DESC))))
				.transform( flow -> logHelper.addFinalLog(flow, printHelper, logger))
				.then();
	}

	private Mono<BookBookshopJpaEntity> createJpaEntity(Map<String, Object> eventData) {
		
		return Mono.fromCallable( () -> {
			
			UUID bookId = mapExtractorHelper.extractUuid(eventData, GlobalConstants.BOOK_ID);
			UUID bookshopId = mapExtractorHelper.extractUuid(eventData, GlobalConstants.BS_ID);
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
		}).onErrorMap(e -> new CreateJpaEntityException(Map.of(GlobalConstants.BBS_CAT, e.getMessage())));		
	}

	private Mono<BookBookshopJpaEntity> insertAction(BookBookshopJpaEntity detachedEntity) {
	    UUID bookId = detachedEntity.getBook().getBookId();
	    UUID bookshopId = detachedEntity.getBookshop().getBookshopId();

	    return Mono.from(sessionFactory.withTransaction((session, tx) ->
	        session.find(BookJpaEntity.class, bookId)
	            .flatMap(book -> session.find(BookshopJpaEntity.class, bookshopId)
	                .map(bookshop -> {
	                    detachedEntity.setBook(book);
	                    detachedEntity.setBookshop(bookshop);
	                    return detachedEntity;
	                }))
	            .flatMap(entity -> session.persist(entity).replaceWith(entity))
	    ).convert().with(UniReactorConverters.toMono()));
	}
	
	@Override
	public Mono<Void> update(Event event) {

		return  Mono.justOrEmpty(event.getEventData())	
				.doOnNext(data -> logHelper.logFlowStep(data, printHelper, logger))
				.flatMap(this::createJpaEntity)
				.doOnNext(data -> logHelper.logFlowStep(data, printHelper, logger))
				.flatMap(this::updateAction)	
				.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(GlobalConstants.BBS_CAT, GlobalConstants.EX_EMPTYRESPONSE_DESC))))
				.transform( flow -> logHelper.addFinalLog(flow, printHelper, logger))
				.then();
	}

	private Mono<BookBookshopJpaEntity> updateAction(BookBookshopJpaEntity entity) {
	
		String query = """
				SELECT
				 e 
				FROM
				 BookBookshopJpaEntity e 				
				WHERE e.bookBookshopId.bookId = ?1 AND 
				e.bookBookshopId.bookshopId = ?2
				""";
		
		return sessionFactory
		.withTransaction((session, tx) -> session.createQuery(query, BookBookshopJpaEntity.class)
				.setParameter(1, entity.getBookBookshopId().getBookId())
				.setParameter(2, entity.getBookBookshopId().getBookshopId())
				.getSingleResult()
	            .flatMap(item -> {
	            	item.setPrice(entity.getPrice());
	            	item.setUnits(entity.getUnits());
	                return session.merge(item);
	            }))
				.convert().with(UniReactorConverters.toMono());	
	}

	@Override
	public Mono<Void> deleteById(Event event) {

		return Mono.justOrEmpty(event.getEventData())
				.doOnNext(data -> logHelper.logFlowStep(data, printHelper, logger))
				.flatMap(this::createJpaEntityCombinedId)
				.doOnNext(data -> logHelper.logFlowStep(data, printHelper, logger))
				.flatMap(this::deleteByIdAction)
				.switchIfEmpty(Mono.error(new EmptyResponseException(
						Map.of(GlobalConstants.BBS_CAT, GlobalConstants.EX_EMPTYRESPONSE_DESC))))
				.transform( flow -> logHelper.addFinalLog(flow, printHelper, logger))
				.then();
	}

	private Mono<BookBookshopId> createJpaEntityCombinedId(Map<String, Object> eventData) {
		
		return Mono.fromCallable( () -> {
			
			UUID bookId = mapExtractorHelper.extractUuid(eventData, GlobalConstants.BOOK_ID);
			UUID bookshopId = mapExtractorHelper.extractUuid(eventData, GlobalConstants.BS_ID);
			
			return new BookBookshopId(bookId, bookshopId);
			
		}).onErrorMap(e -> new CreateJpaEntityException(Map.of(GlobalConstants.BBS_CAT, e.getMessage())));		
	}

	private Mono<BookBookshopJpaEntity> deleteByIdAction(BookBookshopId entityId) {
		
	    return sessionFactory.withTransaction((session, tx) -> session.find(BookBookshopJpaEntity.class, entityId)
	            .onItem().ifNull().failWith(() -> new ResourceNotFoundException(
	                Map.of(GlobalConstants.BBS_ID, entityId.toString())))
	            .call(session::remove))
	            .convert()
	    		.with(UniReactorConverters.toMono());	
	}
}
