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

import dev.ime.common.mapper.AuthorMapper;
import dev.ime.domain.model.Author;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.infrastructure.entity.AuthorJpaEntity;
import io.smallrye.mutiny.converters.uni.UniReactorConverters;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;
import reactor.core.publisher.Mono;

@Repository
@Qualifier("authorReadRepositoryAdapter")
public class AuthorReadRepositoryAdapter implements ReadRepositoryPort<Author> {

	private final SessionFactory sessionFactory;
	private final AuthorMapper mapper;
	
	public AuthorReadRepositoryAdapter(SessionFactory sessionFactory, AuthorMapper mapper) {
		super();
		this.sessionFactory = sessionFactory;
		this.mapper = mapper;
	}

	@Override
	public Mono<Page<Author>> findAll(Pageable pageable) {

	    CriteriaBuilder cb = sessionFactory.getCriteriaBuilder();
	    CriteriaQuery<AuthorJpaEntity> cq = cb.createQuery(AuthorJpaEntity.class);
	    Root<AuthorJpaEntity> root = cq.from(AuthorJpaEntity.class);
	    
	    var booksFetch = root.fetch("books", JoinType.LEFT);
	    booksFetch.fetch("genre", JoinType.LEFT);
	    booksFetch.fetch("publisher", JoinType.LEFT);
	    booksFetch.fetch("authors", JoinType.LEFT);

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
	    Root<AuthorJpaEntity> countRoot = countQuery.from(AuthorJpaEntity.class);
	    countQuery.select(cb.countDistinct(countRoot));

	    var countUni = sessionFactory.withSession(
	        session -> session.createQuery(countQuery).getSingleResult()
	    );

	    Mono<List<AuthorJpaEntity>> entityMono = Mono.from(dataQuery.convert().with(UniReactorConverters.toMono()));
	    Mono<Long> countMono = Mono.from(countUni.convert().with(UniReactorConverters.toMono()));

	    return Mono.zip(entityMono, countMono)
	        .map(tuple -> {
	            List<Author> domList = convertToDom(tuple.getT1());
	            long total = tuple.getT2();
	            return convertToPage(domList, pageable, total);
	        });
	}

	private List<Author> convertToDom(List<AuthorJpaEntity> list) {
		
		return list.stream()
				.map(mapper::fromJpaToDomain)
				.toList();
	}
	
	private Page<Author> convertToPage(List<Author> list, Pageable pageable, long total) {
		
		return new PageImpl<>(list, pageable, total);
	}
	
	private List<Order> buildSortOrder(Sort sort, CriteriaBuilder criteriaBuilder, Root<AuthorJpaEntity> root) {
	
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
	public Mono<Author> findById(UUID id) {

		String queryString = """
				SELECT
				 e
				FROM
				 AuthorJpaEntity e
				LEFT JOIN FETCH e.books b
				LEFT JOIN FETCH b.authors
				LEFT JOIN FETCH b.genre
				LEFT JOIN FETCH b.publisher
				WHERE e.authorId = ?1
				""";

		return sessionFactory
				.withSession(session -> session.createQuery(queryString, AuthorJpaEntity.class)
						.setParameter(1, id)
						.getSingleResultOrNull())
				.convert().with(UniReactorConverters.toMono())
				.map(mapper::fromJpaToDomain);
	}
}
