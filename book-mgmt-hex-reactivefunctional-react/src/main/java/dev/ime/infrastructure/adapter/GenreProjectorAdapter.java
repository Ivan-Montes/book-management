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
import dev.ime.infrastructure.entity.GenreJpaEntity;
import io.smallrye.mutiny.converters.uni.UniReactorConverters;
import reactor.core.publisher.Mono;

@Repository
@Qualifier("genreProjectorAdapter")
public class GenreProjectorAdapter implements ProjectorPort {

	private final SessionFactory sessionFactory;
	private final MapExtractorHelper mapExtractorHelper;
	private final PrintHelper printHelper;
	private final LogHelper logHelper;
	private static final Logger logger = LoggerFactory.getLogger(GenreProjectorAdapter.class);

	public GenreProjectorAdapter(SessionFactory sessionFactory, MapExtractorHelper mapExtractorHelper,
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
				.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(GlobalConstants.GENRE_CAT, GlobalConstants.EX_EMPTYRESPONSE_DESC))))
				.transform( flow -> logHelper.addFinalLog(flow, printHelper, logger))
				.then();
	}

	private Mono<GenreJpaEntity> createJpaEntity(Map<String, Object> eventData) {
		
		return Mono.fromCallable( () -> {
			
			UUID genreId = mapExtractorHelper.extractUuid(eventData, GlobalConstants.GENRE_ID);
			String name = mapExtractorHelper.extractAsString(eventData, GlobalConstants.MODEL_NAME,
					GlobalConstants.PATTERN_NAME_FULL);
			String description = mapExtractorHelper.extractAsString(eventData, GlobalConstants.MODEL_DESC,
					GlobalConstants.PATTERN_DESC_FULL);

			return GenreJpaEntity.builder().genreId(genreId).name(name).description(description).build();
			
		}).onErrorMap(e -> new CreateJpaEntityException(Map.of(GlobalConstants.GENRE_CAT, e.getMessage())));		
	}

	private Mono<GenreJpaEntity> validateCreate(GenreJpaEntity entity) {
		
		return Mono.just(entity)
				.flatMap(this::checkNameAvailability)
				.thenReturn(entity);
	}

	private Mono<GenreJpaEntity> checkNameAvailability(GenreJpaEntity entity) {

		String query = """
				SELECT
				 COUNT(e) 
				FROM
				 GenreJpaEntity e 
				WHERE
				 e.name = :name
				""";
		
		return sessionFactory
				.withSession(
						session -> session.createQuery(query, Long.class).setParameter(GlobalConstants.MODEL_NAME, entity.getName())
								.getSingleResult().map(count -> count == 0))
				.convert().with(UniReactorConverters.toMono()).filter(b -> b)
				.switchIfEmpty(Mono.error(new UniqueValueException(
						Map.of(GlobalConstants.GENRE_NAME, entity.getName()))))
				.thenReturn(entity);
	}
	
	private Mono<GenreJpaEntity> insertAction(GenreJpaEntity entity) {

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
				.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(GlobalConstants.GENRE_CAT, GlobalConstants.EX_EMPTYRESPONSE_DESC))))
				.transform( flow -> logHelper.addFinalLog(flow, printHelper, logger))
				.then();
	}

	private Mono<GenreJpaEntity> validateUpdate(GenreJpaEntity entity) {
		
		return Mono.just(entity)
				.flatMap(this::checkNameAvailabilityInUpdateAction)
				.thenReturn(entity);
	}
	
	private Mono<GenreJpaEntity> checkNameAvailabilityInUpdateAction(GenreJpaEntity entity) {

		String query = """
				SELECT
				 COUNT(e)
				FROM
				 GenreJpaEntity e
				WHERE
				 e.name = :name AND
				 e.genreId != :genreId
				""";

		return sessionFactory
				.withSession(session -> session.createQuery(query, Long.class)
						.setParameter(GlobalConstants.MODEL_NAME, entity.getName())
						.setParameter(GlobalConstants.GENRE_ID, entity.getGenreId()).getSingleResult()
						.map(count -> count == 0))
				.convert().with(UniReactorConverters.toMono()).filter(b -> b)
				.switchIfEmpty(Mono.error(new UniqueValueException(
						Map.of(GlobalConstants.GENRE_NAME, entity.getName()))))
				.thenReturn(entity);
	}
	
	private Mono<GenreJpaEntity> updateAction(GenreJpaEntity entity) {
	
		String query = """
				SELECT
				 e 
				FROM
				 GenreJpaEntity e 
				WHERE
				 e.genreId = :genreId
				""";
		
		return sessionFactory
		.withTransaction((session, tx) -> session.createQuery(query, GenreJpaEntity.class)
				.setParameter(GlobalConstants.GENRE_ID, entity.getGenreId())
				.getSingleResult()
	            .flatMap(item -> {
	            	item.setName(entity.getName());
	            	item.setDescription(entity.getDescription());
	                return session.merge(item);
	            }))
				.convert().with(UniReactorConverters.toMono());	
	}
	
	@Override
	public Mono<Void> deleteById(Event event) {

		return Mono.justOrEmpty(event.getEventData())
				.doOnNext(data -> logHelper.logFlowStep(data, printHelper, logger))
				.map(evenData -> evenData.get(GlobalConstants.GENRE_ID))
				.cast(String.class)
				.map(UUID::fromString)
				.doOnNext(data -> logHelper.logFlowStep(data, printHelper, logger))
				.flatMap(this::validateDelete)	
				.flatMap(this::deleteByIdAction)
				.switchIfEmpty(Mono.error(new EmptyResponseException(
						Map.of(GlobalConstants.GENRE_CAT, GlobalConstants.EX_EMPTYRESPONSE_DESC))))
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
				 BOOKS e 
				WHERE
				 e.genre_id = ?1
				""";		
		
		return sessionFactory
				.withSession(session -> session.createNativeQuery(query, Long.class)
						.setParameter(1, entityId)
						.getSingleResult())
						.convert().with(UniReactorConverters.toMono())
						.filter( i -> i == 0)
						.switchIfEmpty(Mono.error(new EntityAssociatedException(Map.of(GlobalConstants.GENRE_ID, entityId.toString()))))
						.thenReturn(entityId);
	}
	
	private Mono<GenreJpaEntity> deleteByIdAction(UUID entityId) {
		
	    return sessionFactory.withTransaction((session, tx) -> session.find(GenreJpaEntity.class, entityId)
	            .onItem().ifNull().failWith(() -> new ResourceNotFoundException(
	                Map.of(GlobalConstants.GENRE_ID, entityId.toString())))
	            .call(session::remove))
	            .convert()
	    		.with(UniReactorConverters.toMono());	
	}
}
