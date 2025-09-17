package dev.ime.infrastructure.adapter;

import java.util.List;
import java.util.UUID;

import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import dev.ime.common.constants.GlobalConstants;
import dev.ime.common.mapper.BookMapper;
import dev.ime.domain.model.Book;
import dev.ime.domain.port.outbound.BookSpecificReadRepositoryPort;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.infrastructure.entity.BookJpaEntity;
import io.smallrye.mutiny.converters.uni.UniReactorConverters;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import reactor.core.publisher.Mono;

@Repository
@Qualifier("bookReadRepositoryAdapter")
public class BookReadRepositoryAdapter implements ReadRepositoryPort<Book>, BookSpecificReadRepositoryPort {

	private final SessionFactory sessionFactory;
	private final BookMapper mapper;
	
	public BookReadRepositoryAdapter(SessionFactory sessionFactory, BookMapper mapper) {
		super();
		this.sessionFactory = sessionFactory;
		this.mapper = mapper;
	}

	@Override
	public Mono<Page<Book>> findAll(Pageable pageable) {

		CriteriaBuilder cb = sessionFactory.getCriteriaBuilder();
		CriteriaQuery<BookJpaEntity> cq = cb.createQuery(BookJpaEntity.class);
		Root<BookJpaEntity> root = cq.from(BookJpaEntity.class);
		root.fetch("authors", JoinType.LEFT);
		root.fetch("bookshops", JoinType.LEFT);
		root.fetch("publisher", JoinType.LEFT);
		root.fetch("genre", JoinType.LEFT);

		List<Order> orders = buildSortOrder(pageable.getSort(), cb, root);
		cq.orderBy(orders);
	    cq.distinct(true);

	    var dataQuery = sessionFactory.withSession(
	        session -> session.createQuery(cq)
	            .setFirstResult((int) pageable.getOffset())
	            .setMaxResults(pageable.getPageSize())
	            .getResultList()
	    );

	    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
	    Root<BookJpaEntity> countRoot = countQuery.from(BookJpaEntity.class);
	    countQuery.select(cb.countDistinct(countRoot));

	    var countUni = sessionFactory.withSession(
	        session -> session.createQuery(countQuery).getSingleResult()
	    );

	    Mono<List<BookJpaEntity>> entityMono = Mono.from(dataQuery.convert().with(UniReactorConverters.toMono()));
	    Mono<Long> countMono = Mono.from(countUni.convert().with(UniReactorConverters.toMono()));

	    return Mono.zip(entityMono, countMono)
	        .map(tuple -> {
	            List<Book> domList = convertToDom(tuple.getT1());
	            long total = tuple.getT2();
	            return convertToPage(domList, pageable, total);
	        });
	}

	private List<Book> convertToDom(List<BookJpaEntity> list) {
		
		return list.stream()
				.map(mapper::fromJpaToDomain)
				.toList();
	}
	
	private Page<Book> convertToPage(List<Book> list, Pageable pageable, long total) {
		
		return new PageImpl<>(list, pageable, total);
	}
	
	private List<Order> buildSortOrder(Sort sort, CriteriaBuilder criteriaBuilder, Root<BookJpaEntity> root) {
	
		return sort.stream()
		.map( order ->{
			if (order.isAscending()) {
				return criteriaBuilder.asc(root.get(order.getProperty()));
			} else {
				return criteriaBuilder.desc(root.get(order.getProperty()));
			}
		})
		.toList();	
	}

	@Override
	public Mono<Book> findById(UUID id) {

		String queryString = """
				SELECT
				 e
				FROM
				 BookJpaEntity e
				LEFT JOIN FETCH e.authors
				LEFT JOIN FETCH e.bookshops
				LEFT JOIN FETCH e.publisher
				LEFT JOIN FETCH e.genre
				WHERE e.bookId = ?1
				""";

		return sessionFactory
				.withSession(session -> session.createQuery(queryString, BookJpaEntity.class)
						.setParameter(1, id)
						.getSingleResultOrNull())
				.convert().with(UniReactorConverters.toMono())
				.map(mapper::fromJpaToDomain);
	}	
	
	public Mono<Boolean> existsByIsbn(String isbn) {
		
		String jpql = "SELECT COUNT(b) FROM BookJpaEntity b WHERE b.isbn = :isbn";
		return sessionFactory
				.withSession(
						session -> session.createQuery(jpql, Long.class).setParameter(GlobalConstants.BOOK_ISBN, isbn)
								.getSingleResult().map(count -> count != null && count > 0))
				.convert().with(UniReactorConverters.toMono());
	}

	@Override
	public Mono<Boolean> findByIsbnAndIdNot(String isbn, UUID id) {

		CriteriaBuilder cb = sessionFactory.getCriteriaBuilder();
		CriteriaQuery<BookJpaEntity> cq =  cb.createQuery(BookJpaEntity.class);
		Root<BookJpaEntity> root = cq.from(BookJpaEntity.class);
	    Predicate conditionName = cb.equal(root.get(GlobalConstants.BOOK_ISBN), isbn);
	    Predicate conditionId = cb.notEqual(root.get(GlobalConstants.BOOK_ID), id);
	    cq.where(cb.and(conditionName, conditionId));
	    
		return sessionFactory.withSession(session -> session.createQuery(cq)
				.getResultCount().map( count -> count > 0))
				.convert().with(UniReactorConverters.toMono());	
	}
}
