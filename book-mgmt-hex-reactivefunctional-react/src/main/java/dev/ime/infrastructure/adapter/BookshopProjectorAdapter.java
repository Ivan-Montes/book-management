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
import dev.ime.application.exception.EntityAssociatedException;
import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.exception.UniqueValueException;
import dev.ime.application.utils.LogHelper;
import dev.ime.application.utils.MapExtractorHelper;
import dev.ime.application.utils.PrintHelper;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.domain.model.Event;
import dev.ime.domain.port.outbound.ProjectorPort;
import dev.ime.infrastructure.entity.BookshopJpaEntity;
import io.smallrye.mutiny.converters.uni.UniReactorConverters;
import reactor.core.publisher.Mono;

@Repository
@Qualifier("bookshopProjectorAdapter")
public class BookshopProjectorAdapter implements ProjectorPort {

	private final SessionFactory sessionFactory;
	private final MapExtractorHelper mapExtractorHelper;
	private final PrintHelper printHelper;
	private final LogHelper logHelper;
	private static final Logger logger = LoggerFactory.getLogger(BookshopProjectorAdapter.class);

	public BookshopProjectorAdapter(SessionFactory sessionFactory, MapExtractorHelper mapExtractorHelper,
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
				.flatMap(this::validateCreate)
				.flatMap(this::insertAction)
				.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(GlobalConstants.BS_CAT, GlobalConstants.EX_EMPTYRESPONSE_DESC))))
				.transform( flow -> logHelper.addFinalLog(flow, printHelper, logger))
				.then();
	}

	private Mono<BookshopJpaEntity> createJpaEntity(Map<String, Object> eventData) {
		
		return Mono.fromCallable( () -> {
			
			UUID bookshopId = mapExtractorHelper.extractUuid(eventData, GlobalConstants.BS_ID);
			String name = mapExtractorHelper.extractAsString(eventData, GlobalConstants.MODEL_NAME,
					GlobalConstants.PATTERN_NAME_FULL);

			return BookshopJpaEntity.builder().bookshopId(bookshopId).name(name).build();
			
		}).onErrorMap(e -> new CreateJpaEntityException(Map.of(GlobalConstants.BS_CAT, e.getMessage())));		
	}

	private Mono<BookshopJpaEntity> validateCreate(BookshopJpaEntity entity) {
		
		return Mono.just(entity)
				.flatMap(this::checkNameAvailability)
				.thenReturn(entity);
	}

	private Mono<BookshopJpaEntity> checkNameAvailability(BookshopJpaEntity entity) {

		String query = """
				SELECT
				 COUNT(e) 
				FROM
				 BookshopJpaEntity e 
				WHERE
				 e.name = :name
				""";
		
		return sessionFactory
				.withSession(
						session -> session.createQuery(query, Long.class).setParameter(GlobalConstants.MODEL_NAME, entity.getName())
								.getSingleResult().map(count -> count == 0))
				.convert().with(UniReactorConverters.toMono()).filter(b -> b)
				.switchIfEmpty(Mono.error(new UniqueValueException(
						Map.of(GlobalConstants.BS_NAME, entity.getName()))))
				.thenReturn(entity);
	}
	
	private Mono<BookshopJpaEntity> insertAction(BookshopJpaEntity entity) {

		return sessionFactory
		.withTransaction((session, tx) -> session.persist(entity)
				.replaceWith(entity))
		.convert().with(UniReactorConverters.toMono());
	}

	@Override
	public Mono<Void> update(Event event) {

		return  Mono.justOrEmpty(event.getEventData())	
				.doOnNext(data -> logHelper.logFlowStep(data, printHelper, logger))
				.flatMap(this::createJpaEntity)
				.doOnNext(data -> logHelper.logFlowStep(data, printHelper, logger))
				.flatMap(this::validateUpdate)	
				.flatMap(this::updateAction)	
				.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(GlobalConstants.BS_CAT, GlobalConstants.EX_EMPTYRESPONSE_DESC))))
				.transform( flow -> logHelper.addFinalLog(flow, printHelper, logger))
				.then();
	}

	private Mono<BookshopJpaEntity> validateUpdate(BookshopJpaEntity entity) {
		
		return Mono.just(entity)
				.flatMap(this::checkNameAvailabilityInUpdateAction)
				.thenReturn(entity);
	}
	
	private Mono<BookshopJpaEntity> checkNameAvailabilityInUpdateAction(BookshopJpaEntity entity) {

		String query = """
				SELECT
				 COUNT(e)
				FROM
				 BookshopJpaEntity e
				WHERE
				 e.name = :name AND
				 e.bookshopId != :bookshopId
				""";

		return sessionFactory
				.withSession(session -> session.createQuery(query, Long.class)
						.setParameter(GlobalConstants.MODEL_NAME, entity.getName())
						.setParameter(GlobalConstants.BS_ID, entity.getBookshopId()).getSingleResult()
						.map(count -> count == 0))
				.convert().with(UniReactorConverters.toMono()).filter(b -> b)
				.switchIfEmpty(Mono.error(new UniqueValueException(
						Map.of(GlobalConstants.BS_NAME, entity.getName()))))
				.thenReturn(entity);
	}
	
	private Mono<BookshopJpaEntity> updateAction(BookshopJpaEntity entity) {
	
		String query = """
				SELECT
				 e 
				FROM
				 BookshopJpaEntity e 
				WHERE
				 e.bookshopId = :bookshopId
				""";
		
		return sessionFactory
		.withTransaction((session, tx) -> session.createQuery(query, BookshopJpaEntity.class)
				.setParameter(GlobalConstants.BS_ID, entity.getBookshopId())
				.getSingleResult()
	            .flatMap(item -> {
	            	item.setName(entity.getName());
	                return session.merge(item);
	            }))
				.convert().with(UniReactorConverters.toMono());	
	}

	@Override
	public Mono<Void> deleteById(Event event) {

		return Mono.justOrEmpty(event.getEventData())
				.doOnNext(data -> logHelper.logFlowStep(data, printHelper, logger))
				.map(evenData -> evenData.get(GlobalConstants.BS_ID))
				.cast(String.class)
				.map(UUID::fromString)
				.doOnNext(data -> logHelper.logFlowStep(data, printHelper, logger))
				.flatMap(this::validateDelete)	
				.flatMap(this::deleteByIdAction)
				.switchIfEmpty(Mono.error(new EmptyResponseException(
						Map.of(GlobalConstants.BS_CAT, GlobalConstants.EX_EMPTYRESPONSE_DESC))))
				.transform( flow -> logHelper.addFinalLog(flow, printHelper, logger))
				.then();
	}

	private Mono<UUID> validateDelete(UUID entityId) {
		
		return Mono.just(entityId)
				.flatMap(this::checkEntityAssociation)
				.thenReturn(entityId);
	}
	
	private Mono<UUID> checkEntityAssociation(UUID entityId){
		
		String query = """
				SELECT
				 COUNT(*)
				FROM
				 BOOKS_BOOKSHOPS e 
				WHERE
				 e.bookshop_bookshop_id = ?1
				""";		
		
		return sessionFactory
				.withSession(session -> session.createNativeQuery(query, Long.class)
						.setParameter(1, entityId)
						.getSingleResult())
						.convert().with(UniReactorConverters.toMono())
						.filter( i -> i == 0)
						.switchIfEmpty(Mono.error(new EntityAssociatedException(Map.of(GlobalConstants.BS_ID, entityId.toString()))))
						.thenReturn(entityId);
	}
	
	private Mono<BookshopJpaEntity> deleteByIdAction(UUID entityId) {
		
	    return sessionFactory.withTransaction((session, tx) -> session.find(BookshopJpaEntity.class, entityId)
	            .onItem().ifNull().failWith(() -> new ResourceNotFoundException(
	                Map.of(GlobalConstants.BS_ID, entityId.toString())))
	            .call(session::remove))
	            .convert()
	    		.with(UniReactorConverters.toMono());	
	}
}
