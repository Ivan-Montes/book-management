package dev.ime.infrastructure.adapter;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
import dev.ime.infrastructure.entity.AuthorJpaEntity;
import dev.ime.infrastructure.entity.BookJpaEntity;
import dev.ime.infrastructure.entity.GenreJpaEntity;
import dev.ime.infrastructure.entity.PublisherJpaEntity;
import io.smallrye.mutiny.converters.uni.UniReactorConverters;
import reactor.core.publisher.Mono;

@Repository
@Qualifier("bookProjectorAdapter")
public class BookProjectorAdapter implements ProjectorPort {

	private final SessionFactory sessionFactory;
	private final MapExtractorHelper mapExtractorHelper;
	private final PrintHelper printHelper;
	private final LogHelper logHelper;
	private static final Logger logger = LoggerFactory.getLogger(BookProjectorAdapter.class);

	public BookProjectorAdapter(SessionFactory sessionFactory, MapExtractorHelper mapExtractorHelper,
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
				.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(GlobalConstants.BOOK_CAT, GlobalConstants.EX_EMPTYRESPONSE_DESC))))
				.transform( flow -> logHelper.addFinalLog(flow, printHelper, logger))
				.then();
	}

	private Mono<BookJpaEntity> createJpaEntity(Map<String, Object> eventData) {
		
		return Mono.fromCallable( () -> {
			
			UUID bookId = mapExtractorHelper.extractUuid(eventData, GlobalConstants.BOOK_ID);
			String isbn = mapExtractorHelper.extractAsString(eventData, GlobalConstants.BOOK_ISBN,
					GlobalConstants.PATTERN_ISBN);
			String title = mapExtractorHelper.extractAsString(eventData, GlobalConstants.BOOK_TITLE,
					GlobalConstants.PATTERN_TITLE_FULL);
			UUID publisherId = mapExtractorHelper.extractUuid(eventData, GlobalConstants.PUBLI_ID);
			UUID genreId = mapExtractorHelper.extractUuid(eventData, GlobalConstants.GENRE_ID);
			Set<UUID> authorIdSet = mapExtractorHelper.extractRequiredUuidSet(eventData, GlobalConstants.AUTHOR_IDSET);
			
			PublisherJpaEntity publisherJpaEntity = new PublisherJpaEntity();
			publisherJpaEntity.setPublisherId(publisherId);
			
			GenreJpaEntity genreJpaEntity = new GenreJpaEntity();
			genreJpaEntity.setGenreId(genreId);
			
			Set<AuthorJpaEntity> authors = createAuthors(authorIdSet);
					
			return BookJpaEntity.builder().bookId(bookId).isbn(isbn).title(title).publisher(publisherJpaEntity).genre(genreJpaEntity).authors(authors).build();
		}).onErrorMap(e -> new CreateJpaEntityException(Map.of(GlobalConstants.BOOK_CAT, e.getMessage())));		
	}

	private Set<AuthorJpaEntity> createAuthors(Set<UUID> authorIdSet) {
		
		return authorIdSet.stream()
		.map( id -> {
			AuthorJpaEntity entity = new AuthorJpaEntity();
			entity.setAuthorId(id);
			return entity;
		})
		.collect(Collectors.toSet());		
	}
	
	private Mono<BookJpaEntity> insertAction(BookJpaEntity entity) {

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
				.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(GlobalConstants.BOOK_CAT, GlobalConstants.EX_EMPTYRESPONSE_DESC))))
				.transform( flow -> logHelper.addFinalLog(flow, printHelper, logger))
				.then();
	}

	private Mono<BookJpaEntity> updateAction(BookJpaEntity entity) {
	
		String query = """
				SELECT
				 e 
				FROM
				 BookJpaEntity e 
				WHERE
				 e.bookId = :bookId
				""";
		
		return sessionFactory.withTransaction((session, tx) -> session.createQuery(query, BookJpaEntity.class)
				.setParameter(GlobalConstants.BOOK_ID, entity.getBookId())
				.getSingleResult()
				.flatMap(item -> {
					item.setIsbn(entity.getIsbn());
					item.setTitle(entity.getTitle());
					item.setPublisher(entity.getPublisher());
					item.setGenre(entity.getGenre());
					item.setAuthors(entity.getAuthors());
					return session.merge(item);
				})).convert().with(UniReactorConverters.toMono());
	}

	@Override
	public Mono<Void> deleteById(Event event) {

		return Mono.justOrEmpty(event.getEventData())
				.doOnNext(data -> logHelper.logFlowStep(data, printHelper, logger))
				.map(evenData -> evenData.get(GlobalConstants.BOOK_ID))
				.cast(String.class)
				.map(UUID::fromString)
				.doOnNext(data -> logHelper.logFlowStep(data, printHelper, logger))
				.flatMap(this::deleteByIdAction)
				.switchIfEmpty(Mono.error(new EmptyResponseException(
						Map.of(GlobalConstants.BOOK_CAT, GlobalConstants.EX_EMPTYRESPONSE_DESC))))
				.transform( flow -> logHelper.addFinalLog(flow, printHelper, logger))
				.then();
	}
	
	private Mono<BookJpaEntity> deleteByIdAction(UUID entityId) {
		
	    return sessionFactory.withTransaction((session, tx) -> session.find(BookJpaEntity.class, entityId)
	            .onItem().ifNull().failWith(() -> new ResourceNotFoundException(
	                Map.of(GlobalConstants.BOOK_ID, entityId.toString())))
	            .call(session::remove))
	            .convert()
	    		.with(UniReactorConverters.toMono());	
	}
}
