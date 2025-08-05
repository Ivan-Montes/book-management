package dev.ime.adapter;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import dev.ime.common.GlobalConstants;
import dev.ime.entity.AuthorJpaEntity;
import dev.ime.entity.BookJpaEntity;
import dev.ime.entity.GenreJpaEntity;
import dev.ime.entity.PublisherJpaEntity;
import dev.ime.mapper.BookMapper;
import dev.ime.model.Author;
import dev.ime.model.Book;
import dev.ime.model.Genre;
import dev.ime.model.Publisher;
import dev.ime.port.RepositoryPort;
import dev.ime.repository.AuthorRepository;
import dev.ime.repository.BookRepository;
import dev.ime.repository.GenreRepository;
import dev.ime.repository.PublisherRepository;

@Repository
public class BookRepositoryAdapter implements RepositoryPort<Book> {

	private final BookRepository bookRepository;
	private final BookMapper mapper;
	private final PublisherRepository publisherRepository;
	private final GenreRepository genreRepository;
	private final AuthorRepository authorRepository;
	private static final Logger logger = LoggerFactory.getLogger(BookRepositoryAdapter.class);

	public BookRepositoryAdapter(BookRepository bookRepository, BookMapper mapper,
			PublisherRepository publisherRepository, GenreRepository genreRepository,
			AuthorRepository authorRepository) {
		super();
		this.bookRepository = bookRepository;
		this.mapper = mapper;
		this.publisherRepository = publisherRepository;
		this.genreRepository = genreRepository;
		this.authorRepository = authorRepository;
	}

	@Override
	public Page<Book> findAll(Pageable pageable) {

		Page<BookJpaEntity> page = bookRepository.findAll(pageable);
		List<Book> contentList = page.getContent().stream().map(mapper::fromJpaToDomain).toList();

		return new PageImpl<>(contentList, pageable, page.getTotalElements());
	}

	@Override
	public Optional<Book> findById(Long id) {

		return bookRepository.findById(id).map(mapper::fromJpaToDomain);
	}

	@Override
	public Optional<Book> save(Book item) {
		
		Optional<PublisherJpaEntity> optPub = findPublisher(item);
		if (optPub.isEmpty()) {
			logger.info(GlobalConstants.MSG_PATTERN_INFO, GlobalConstants.EX_RESOURCENOTFOUND_DESC, GlobalConstants.PUBLI_ID);
			return Optional.empty();
		}
		
		Optional<GenreJpaEntity> optGen = findGenre(item);
		if (optGen.isEmpty()) {
			logger.info(GlobalConstants.MSG_PATTERN_INFO, GlobalConstants.EX_RESOURCENOTFOUND_DESC, GlobalConstants.GENRE_ID);
			return Optional.empty();
		}
		
		Set<AuthorJpaEntity> authors = findAuthors(item);
		if ( authors.isEmpty()) {
			logger.info(GlobalConstants.MSG_PATTERN_INFO, GlobalConstants.EX_RESOURCENOTFOUND_DESC, GlobalConstants.AUTHOR_ID);
			return Optional.empty();
		}
		
		BookJpaEntity jpa = mapper.fromDomainToJpa(item);
		jpa.setPublisher(optPub.get());
		jpa.setGenre(optGen.get());
		jpa.setAuthors(authors);
		
		return Optional.ofNullable(bookRepository.save(jpa)).map(mapper::fromJpaToDomain);
	}

	private Optional<PublisherJpaEntity> findPublisher(Book item) {
		
		Publisher publisher = item.getPublisher();
		if (publisher == null) {
			return Optional.empty();
		}
		
		Long id = publisher.getPublisherId();
		if (id == null) {
			return Optional.empty();
		}
		
		return publisherRepository.findById(id);				
	}

	private Optional<GenreJpaEntity> findGenre(Book item) {
		
		Genre genre = item.getGenre();
		if (genre == null) {
			return Optional.empty();
		}
		
		Long id = genre.getGenreId();
		if (id == null) {
			return Optional.empty();
		}
		
		return genreRepository.findById(id);				
	}
	
	private Set<AuthorJpaEntity> findAuthors(Book item) {

		Set<Author> authors = item.getAuthors();
		
		if (authors == null || authors.isEmpty()) {
			logger.info(GlobalConstants.MSG_PATTERN_INFO, GlobalConstants.MSG_COLLECT, GlobalConstants.MSG_EMPTY);
			return new HashSet<>();	
		}
		
		return authors.stream()
		.map(Author::getAuthorId)
		.map(authorRepository::findById)
		.filter(Optional::isPresent)
		.map(Optional::get)
		.collect(Collectors.toSet());		
	}
	
	@Override
	public Optional<Book> update(Book item) {

		Optional<BookJpaEntity> opt = bookRepository.findById(item.getBookId());
		if (opt.isEmpty()) {
			return Optional.empty();
		}
		BookJpaEntity jpaEntity = opt.get();
		jpaEntity.setIsbn(item.getIsbn());
		jpaEntity.setTitle(item.getTitle());
		findAndSetPublisher(jpaEntity, item);
		findAndSetGenre(jpaEntity, item);
		findAndSetAuthors(jpaEntity, item);

		return Optional.ofNullable(bookRepository.save(jpaEntity)).map(mapper::fromJpaToDomain);
	}

	private void findAndSetPublisher(BookJpaEntity jpaEntity, Book item) {
		
		var publisher = item.getPublisher();
		if (publisher == null) {
			return;
		}
		
		Long id = publisher.getPublisherId();
		if (id == null) {
			return;
		}
		
		var entity = publisherRepository.findById(id);
		if (entity.isPresent()) {
			jpaEntity.setPublisher(entity.get());
		}			
	}

	private void findAndSetGenre(BookJpaEntity jpaEntity, Book item) {
		
		var genre = item.getGenre();
		if (genre == null) {
			return;
		}
		
		Long id = genre.getGenreId();
		if (id == null) {
			return;
		}
		
		var entity = genreRepository.findById(id);
		if (entity.isPresent()) {
			jpaEntity.setGenre(entity.get());
		}		
	}

	private void findAndSetAuthors(BookJpaEntity jpaEntity, Book item) {
		
		Set<Author> authors = item.getAuthors();
		
		if (authors == null || authors.isEmpty()) {
			logger.info(GlobalConstants.MSG_PATTERN_INFO, GlobalConstants.MSG_COLLECT, GlobalConstants.MSG_EMPTY);
			return;		
		}
		
		var authorJpaEntitySet = authors.stream()
		.map(Author::getAuthorId)
		.map(authorRepository::findById)
		.filter(Optional::isPresent)
		.map(Optional::get)
		.collect(Collectors.toSet());
		
		if (!authorJpaEntitySet.isEmpty()) {
			jpaEntity.setAuthors(authorJpaEntitySet);
		}
	}
	
	@Override
	public boolean deleteById(Long id) {

		bookRepository.deleteById(id);
		return bookRepository.findById(id).isEmpty();
	}
}
