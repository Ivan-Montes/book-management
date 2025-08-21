package dev.ime.infrastructure.adapter;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
import dev.ime.infrastructure.entity.BookJpaEntity;
import dev.ime.infrastructure.entity.GenreJpaEntity;
import dev.ime.infrastructure.entity.PublisherJpaEntity;
import dev.ime.infrastructure.repository.BookRepository;

@Repository
@Qualifier("bookProjectorAdapter")
public class BookProjectorAdapter implements ProjectorPort {

	private final BookRepository bookRepository;
	private final MapExtractorHelper mapExtractorHelper;
	private final PrintHelper printHelper;
	private static final Logger logger = LoggerFactory.getLogger(BookProjectorAdapter.class);
	
	public BookProjectorAdapter(BookRepository bookRepository, MapExtractorHelper mapExtractorHelper,
			PrintHelper printHelper) {
		super();
		this.bookRepository = bookRepository;
		this.mapExtractorHelper = mapExtractorHelper;
		this.printHelper = printHelper;
	}

	@Override
	public void create(Event event) {

		BookJpaEntity item = createJpaEntityForInsert(event.getEventData());
		BookJpaEntity jpaEntitySaved = bookRepository.save(item);
		printHelper.printResult(event.getEventType(), jpaEntitySaved, logger);
	}

	private BookJpaEntity createJpaEntityForInsert(Map<String, Object> eventData) {

		String isbn = mapExtractorHelper.extractAsString(eventData, GlobalConstants.BOOK_ISBN,
				GlobalConstants.PATTERN_ISBN);
		String title = mapExtractorHelper.extractAsString(eventData, GlobalConstants.BOOK_TITLE,
				GlobalConstants.PATTERN_TITLE_FULL);
		Long publisherId = Long.valueOf(
				mapExtractorHelper.extractAsString(eventData, GlobalConstants.PUBLI_ID, GlobalConstants.PATTERN_ID));
		Long genreId = Long.valueOf(
				mapExtractorHelper.extractAsString(eventData, GlobalConstants.GENRE_ID, GlobalConstants.PATTERN_ID));
		Set<Long> authorIdSet = mapExtractorHelper.extractRequiredLongSet(eventData, GlobalConstants.AUTHOR_IDSET);
		
		PublisherJpaEntity publisherJpaEntity = new PublisherJpaEntity();
		publisherJpaEntity.setPublisherId(publisherId);
		
		GenreJpaEntity genreJpaEntity = new GenreJpaEntity();
		genreJpaEntity.setGenreId(genreId);
		
		Set<AuthorJpaEntity> authors = createAuthors(authorIdSet);
				
		return BookJpaEntity.builder().isbn(isbn).title(title).publisher(publisherJpaEntity).genre(genreJpaEntity).authors(authors).build();
		
	}

	private Set<AuthorJpaEntity> createAuthors(Set<Long> authorIdSet) {
		
		return authorIdSet.stream()
		.map( id -> {
			AuthorJpaEntity entity = new AuthorJpaEntity();
			entity.setAuthorId(id);
			return entity;
		})
		.collect(Collectors.toSet());		
	}
	
	@Override
	public void update(Event event) {

		BookJpaEntity item = createJpaEntityForUpdate(event.getEventData());
		Optional<BookJpaEntity> opt = bookRepository.findById(item.getBookId());
		if (opt.isEmpty()) {
			logger.error(GlobalConstants.MSG_PATTERN_SEVERE, event.getEventType(), item);
			return;
		}
		BookJpaEntity jpaEntity = opt.get();
		jpaEntity.setIsbn(item.getIsbn());
		jpaEntity.setTitle(item.getTitle());
		jpaEntity.setPublisher(item.getPublisher());
		jpaEntity.setGenre(item.getGenre());
		jpaEntity.setAuthors(item.getAuthors());

		BookJpaEntity jpaEntitySaved = bookRepository.save(jpaEntity);
		printHelper.printResult(event.getEventType(), jpaEntitySaved, logger);
	}

	private BookJpaEntity createJpaEntityForUpdate(Map<String, Object> eventData) {

		Long bookId = Long.valueOf(
				mapExtractorHelper.extractAsString(eventData, GlobalConstants.BOOK_ID, GlobalConstants.PATTERN_ID));
		String isbn = mapExtractorHelper.extractAsString(eventData, GlobalConstants.BOOK_ISBN,
				GlobalConstants.PATTERN_ISBN);
		String title = mapExtractorHelper.extractAsString(eventData, GlobalConstants.BOOK_TITLE,
				GlobalConstants.PATTERN_TITLE_FULL);
		Long publisherId = Long.valueOf(
				mapExtractorHelper.extractAsString(eventData, GlobalConstants.PUBLI_ID, GlobalConstants.PATTERN_ID));
		Long genreId = Long.valueOf(
				mapExtractorHelper.extractAsString(eventData, GlobalConstants.GENRE_ID, GlobalConstants.PATTERN_ID));
		Set<Long> authorIdSet = mapExtractorHelper.extractRequiredLongSet(eventData, GlobalConstants.AUTHOR_IDSET);
		
		PublisherJpaEntity publisherJpaEntity = new PublisherJpaEntity();
		publisherJpaEntity.setPublisherId(publisherId);
		
		GenreJpaEntity genreJpaEntity = new GenreJpaEntity();
		genreJpaEntity.setGenreId(genreId);
		
		Set<AuthorJpaEntity> authors = createAuthors(authorIdSet);
				
		return BookJpaEntity.builder().bookId(bookId).isbn(isbn).title(title).publisher(publisherJpaEntity).genre(genreJpaEntity).authors(authors).build();
	}

	@Override
	public void deleteById(Event event) {

		Long bookId = Long.valueOf(mapExtractorHelper.extractAsString(event.getEventData(), GlobalConstants.BOOK_ID,
				GlobalConstants.PATTERN_ID));
		Optional<BookJpaEntity> opt = bookRepository.findById(bookId);
		if (opt.isEmpty()) {
			logger.error(GlobalConstants.MSG_PATTERN_SEVERE, event.getEventType(), bookId);
			return;
		}
		bookRepository.deleteById(bookId);
		boolean boolResult = bookRepository.findById(bookId).isEmpty();
		printHelper.printResult(event.getEventType(), boolResult, logger);
	}
}
