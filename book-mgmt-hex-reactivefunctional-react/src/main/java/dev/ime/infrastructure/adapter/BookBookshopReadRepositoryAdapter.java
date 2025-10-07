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

import dev.ime.common.mapper.BookBookshopMapper;
import dev.ime.domain.model.BookBookshop;
import dev.ime.domain.port.outbound.CompositeIdReadRepositoryPort;
import dev.ime.infrastructure.entity.BookBookshopJpaEntity;
import io.smallrye.mutiny.converters.uni.UniReactorConverters;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;
import reactor.core.publisher.Mono;

@Repository
@Qualifier("bookBookshopReadRepositoryAdapter")
public class BookBookshopReadRepositoryAdapter implements CompositeIdReadRepositoryPort<BookBookshop> {

	private final SessionFactory sessionFactory;
    private final BookBookshopMapper mapper;
    
	public BookBookshopReadRepositoryAdapter(SessionFactory sessionFactory, BookBookshopMapper mapper) {
		super();
		this.sessionFactory = sessionFactory;
		this.mapper = mapper;
	}

	@Override
	public Mono<Page<BookBookshop>> findAll(Pageable pageable) {

		CriteriaBuilder cb = sessionFactory.getCriteriaBuilder();
		CriteriaQuery<BookBookshopJpaEntity> cq = cb.createQuery(BookBookshopJpaEntity.class);
		Root<BookBookshopJpaEntity> root = cq.from(BookBookshopJpaEntity.class);
		
	    var booksFetch = root.fetch("book", JoinType.LEFT);
	    booksFetch.fetch("genre", JoinType.LEFT);
	    booksFetch.fetch("publisher", JoinType.LEFT);
		root.fetch("bookshop", JoinType.LEFT);

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
	    Root<BookBookshopJpaEntity> countRoot = countQuery.from(BookBookshopJpaEntity.class);
	    countQuery.select(cb.countDistinct(countRoot));

	    var countUni = sessionFactory.withSession(
	        session -> session.createQuery(countQuery).getSingleResult()
	    );

	    Mono<List<BookBookshopJpaEntity>> entityMono = Mono.from(dataQuery.convert().with(UniReactorConverters.toMono()));
	    Mono<Long> countMono = Mono.from(countUni.convert().with(UniReactorConverters.toMono()));

	    return Mono.zip(entityMono, countMono)
	        .map(tuple -> {
	            List<BookBookshop> domList = convertToDom(tuple.getT1());
	            long total = tuple.getT2();
	            return convertToPage(domList, pageable, total);
	        });
	}

	private List<BookBookshop> convertToDom(List<BookBookshopJpaEntity> list) {
		
		return list.stream()
				.map(mapper::fromJpaToDomain)
				.toList();
	}
	
	private Page<BookBookshop> convertToPage(List<BookBookshop> list, Pageable pageable, long total) {
		
		return new PageImpl<>(list, pageable, total);
	}
	
	private List<Order> buildSortOrder(Sort sort, CriteriaBuilder criteriaBuilder, Root<BookBookshopJpaEntity> root) {
	
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
	public Mono<BookBookshop> findById(UUID id01, UUID id02) {

		String queryString = """
				SELECT
				 e
				FROM
				 BookBookshopJpaEntity e
				LEFT JOIN FETCH e.book b
				LEFT JOIN FETCH b.genre
				LEFT JOIN FETCH b.publisher
				LEFT JOIN FETCH e.bookshop
				WHERE e.bookBookshopId.bookId = ?1 AND
				e.bookBookshopId.bookshopId = ?2
				""";

		return sessionFactory
				.withSession(session -> session.createQuery(queryString, BookBookshopJpaEntity.class)
						.setParameter(1, id01)
						.setParameter(2, id02)
						.getSingleResultOrNull())
				.convert().with(UniReactorConverters.toMono())
				.map(mapper::fromJpaToDomain);
	}    
}
