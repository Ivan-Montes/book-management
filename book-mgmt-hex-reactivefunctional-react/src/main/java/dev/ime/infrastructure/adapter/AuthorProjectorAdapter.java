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
import dev.ime.application.utils.LogHelper;
import dev.ime.application.utils.MapExtractorHelper;
import dev.ime.application.utils.PrintHelper;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.domain.model.Event;
import dev.ime.domain.port.outbound.ProjectorPort;
import dev.ime.infrastructure.entity.AuthorJpaEntity;
import io.smallrye.mutiny.converters.uni.UniReactorConverters;
import reactor.core.publisher.Mono;

@Repository
@Qualifier("authorProjectorAdapter")
public class AuthorProjectorAdapter implements ProjectorPort {

	private final SessionFactory sessionFactory;
	private final MapExtractorHelper mapExtractorHelper;
	private final PrintHelper printHelper;
	private final LogHelper logHelper;
	private static final Logger logger = LoggerFactory.getLogger(AuthorProjectorAdapter.class);

	public AuthorProjectorAdapter(SessionFactory sessionFactory, MapExtractorHelper mapExtractorHelper,
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
				.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(GlobalConstants.AUTHOR_CAT, GlobalConstants.EX_EMPTYRESPONSE_DESC))))
				.transform( flow -> logHelper.addFinalLog(flow, printHelper, logger))
				.then();
	}

	private Mono<AuthorJpaEntity> createJpaEntity(Map<String, Object> eventData) {
		
		return Mono.fromCallable( () -> {
			
			UUID authorId = mapExtractorHelper.extractUuid(eventData, GlobalConstants.AUTHOR_ID);
			String name = mapExtractorHelper.extractAsString(eventData, GlobalConstants.MODEL_NAME,
					GlobalConstants.PATTERN_NAME_FULL);
			String surname = mapExtractorHelper.extractAsString(eventData, GlobalConstants.MODEL_SURNAME,
					GlobalConstants.PATTERN_SURNAME_FULL);

			return AuthorJpaEntity.builder().authorId(authorId).name(name).surname(surname).build();
			
		}).onErrorMap(e -> new CreateJpaEntityException(Map.of(GlobalConstants.AUTHOR_CAT, e.getMessage())));		
	}

	private Mono<AuthorJpaEntity> insertAction(AuthorJpaEntity entity) {

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
				.flatMap(this::updateAction)	
				.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(GlobalConstants.AUTHOR_CAT, GlobalConstants.EX_EMPTYRESPONSE_DESC))))
				.transform( flow -> logHelper.addFinalLog(flow, printHelper, logger))
				.then();
	}

	private Mono<AuthorJpaEntity> updateAction(AuthorJpaEntity entity) {
	
		String query = """
				SELECT
				 e 
				FROM
				 AuthorJpaEntity e 
				WHERE
				 e.authorId = :authorId
				""";
		
		return sessionFactory
		.withTransaction((session, tx) -> session.createQuery(query, AuthorJpaEntity.class)
				.setParameter(GlobalConstants.AUTHOR_ID, entity.getAuthorId())
				.getSingleResult()
	            .flatMap(item -> {
	            	item.setName(entity.getName());
	            	item.setSurname(entity.getSurname());
	                return session.merge(item);
	            }))
				.convert().with(UniReactorConverters.toMono());	
	}
	
	@Override
	public Mono<Void> deleteById(Event event) {
		
		return Mono.justOrEmpty(event.getEventData())
				.doOnNext(data -> logHelper.logFlowStep(data, printHelper, logger))
				.map(evenData -> evenData.get(GlobalConstants.AUTHOR_ID))
				.cast(String.class)
				.map(UUID::fromString)
				.doOnNext(data -> logHelper.logFlowStep(data, printHelper, logger))
				.flatMap(this::validateDelete)	
				.flatMap(this::deleteByIdAction)
				.switchIfEmpty(Mono.error(new EmptyResponseException(
						Map.of(GlobalConstants.AUTHOR_CAT, GlobalConstants.EX_EMPTYRESPONSE_DESC))))
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
				 BOOKS_AUTHORS e 
				WHERE
				 e.author_id = ?1
				""";		
		
		return sessionFactory
				.withSession(session -> session.createNativeQuery(query, Long.class)
						.setParameter(1, entityId)
						.getSingleResult())
						.convert().with(UniReactorConverters.toMono())
						.filter( i -> i == 0)
						.switchIfEmpty(Mono.error(new EntityAssociatedException(Map.of(GlobalConstants.AUTHOR_ID, entityId.toString()))))
						.thenReturn(entityId);
	}
	
	private Mono<AuthorJpaEntity> deleteByIdAction(UUID entityId) {
		
	    return sessionFactory.withTransaction((session, tx) -> session.find(AuthorJpaEntity.class, entityId)
	            .onItem().ifNull().failWith(() -> new ResourceNotFoundException(
	                Map.of(GlobalConstants.AUTHOR_ID, entityId.toString())))
	            .call(session::remove))
	            .convert()
	    		.with(UniReactorConverters.toMono());	
	}
}
