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
import dev.ime.application.exception.EntityAssociatedException;
import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.exception.UniqueValueException;
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
import reactor.core.publisher.Flux;
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
				.flatMap(this::validateCreate)
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

	private Mono<BookJpaEntity> validateCreate(BookJpaEntity entity) {
		
		return Mono.just(entity)
				.flatMap(this::checkExistsIsbn)
				.flatMap(this::checkExistsGenre)
				.flatMap(this::checkExistsPublisher)
				.flatMap(this::checkExistsAuthors)
				.thenReturn(entity);
	}

	private Mono<BookJpaEntity> checkExistsIsbn(BookJpaEntity entity) {

		String query = """
				SELECT
				 COUNT(e) 
				FROM
				 BookJpaEntity e 
				WHERE
				 e.isbn = :isbn
				""";
		
		return sessionFactory
				.withSession(
						session -> session.createQuery(query, Long.class)
						.setParameter(GlobalConstants.BOOK_ISBN, entity.getIsbn())
						.getSingleResult().map(count -> count == 0))
				.convert().with(UniReactorConverters.toMono()).filter(b -> b)
				.switchIfEmpty(Mono.error(new UniqueValueException(
						Map.of(GlobalConstants.BOOK_ISBN, entity.getIsbn()))))
				.thenReturn(entity);
	}

	private Mono<BookJpaEntity> checkExistsGenre(BookJpaEntity entity) {

		String query = """
				SELECT
				 COUNT(e) 
				FROM
				 GenreJpaEntity e 
				WHERE
				 e.genreId = :genreId
				""";
		
		return sessionFactory
				.withSession(
						session -> session.createQuery(query, Long.class)
						.setParameter(GlobalConstants.GENRE_ID, entity.getGenre().getGenreId())
						.getSingleResult().map(count -> count > 0))
				.convert().with(UniReactorConverters.toMono()).filter(b -> b)
				.switchIfEmpty(Mono.error(new ResourceNotFoundException(
						Map.of(GlobalConstants.GENRE_ID, entity.getGenre().getGenreId().toString()))))
				.thenReturn(entity);
	}

	private Mono<BookJpaEntity> checkExistsPublisher(BookJpaEntity entity) {

		String query = """
				SELECT
				 COUNT(e) 
				FROM
				 PublisherJpaEntity e 
				WHERE
				 e.publisherId = :publisherId
				""";
		
		return sessionFactory
				.withSession(
						session -> session.createQuery(query, Long.class)
						.setParameter(GlobalConstants.PUBLI_ID, entity.getPublisher().getPublisherId())
						.getSingleResult().map(count -> count > 0))
				.convert().with(UniReactorConverters.toMono()).filter(b -> b)
				.switchIfEmpty(Mono.error(new ResourceNotFoundException(
						Map.of(GlobalConstants.PUBLI_ID, entity.getPublisher().getPublisherId().toString()))))
				.thenReturn(entity);
	}

	private Mono<BookJpaEntity> checkExistsAuthors(BookJpaEntity entity) {
	    return Flux.fromIterable(entity.getAuthors())
	    		.map(AuthorJpaEntity::getAuthorId)
	            .concatMap(this::checkExistsAuthorById)
	            .then()
	    		.thenReturn(entity); 
	}
	
	private Mono<UUID> checkExistsAuthorById(UUID authorId) {

		String query = """
				SELECT
				 COUNT(e) 
				FROM
				 AuthorJpaEntity e 
				WHERE
				 e.authorId = :authorId
				""";
		
		return sessionFactory
				.withSession(
						session -> session.createQuery(query, Long.class)
						.setParameter(GlobalConstants.AUTHOR_ID, authorId)
						.getSingleResult().map(count -> count > 0))
				.convert().with(UniReactorConverters.toMono()).filter(b -> b)
				.switchIfEmpty(Mono.error(new ResourceNotFoundException(
						Map.of(GlobalConstants.AUTHOR_ID, authorId.toString()))))
				.thenReturn(authorId);
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
				.flatMap(this::validateUpdate)	
				.flatMap(this::updateAction)	
				.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(GlobalConstants.BOOK_CAT, GlobalConstants.EX_EMPTYRESPONSE_DESC))))
				.transform( flow -> logHelper.addFinalLog(flow, printHelper, logger))
				.then();
	}

	private Mono<BookJpaEntity> validateUpdate(BookJpaEntity entity) {
		
		return Mono.just(entity)
				.flatMap(this::checkExistsByIsbnAndIdNot)
				.flatMap(this::checkExistsGenre)
				.flatMap(this::checkExistsPublisher)
				.flatMap(this::checkExistsAuthors)
				.thenReturn(entity);
	}

	private Mono<BookJpaEntity> checkExistsByIsbnAndIdNot(BookJpaEntity entity) {

		String query = """
				SELECT
				 COUNT(e) 
				FROM
				 BookJpaEntity e 
				WHERE
				 e.isbn = :isbn AND
				 e.bookId != :bookId
				""";
		
		return sessionFactory
				.withSession(
						session -> session.createQuery(query, Long.class)
						.setParameter(GlobalConstants.BOOK_ISBN, entity.getIsbn())
						.setParameter(GlobalConstants.BOOK_ID, entity.getBookId())
						.getSingleResult().map(count -> count == 0))
				.convert().with(UniReactorConverters.toMono()).filter(b -> b)
				.switchIfEmpty(Mono.error(new UniqueValueException(
						Map.of(GlobalConstants.BOOK_ISBN, entity.getIsbn()))))
				.thenReturn(entity);
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
				.flatMap(this::validateDelete)	
				.flatMap(this::deleteByIdAction)
				.switchIfEmpty(Mono.error(new EmptyResponseException(
						Map.of(GlobalConstants.BOOK_CAT, GlobalConstants.EX_EMPTYRESPONSE_DESC))))
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
				 e.book_book_id = ?1
				""";		
		
		return sessionFactory
				.withSession(session -> session.createNativeQuery(query, Long.class)
						.setParameter(1, entityId)
						.getSingleResult())
						.convert().with(UniReactorConverters.toMono())
						.filter( i -> i == 0)
						.switchIfEmpty(Mono.error(new EntityAssociatedException(Map.of(GlobalConstants.BOOK_ID, entityId.toString()))))
						.thenReturn(entityId);
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
