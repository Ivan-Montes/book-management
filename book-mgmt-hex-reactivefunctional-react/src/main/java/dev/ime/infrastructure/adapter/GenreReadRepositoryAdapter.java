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
import dev.ime.common.mapper.GenreMapper;
import dev.ime.domain.model.Genre;
import dev.ime.domain.port.outbound.ReadRepositoryPort;
import dev.ime.domain.port.outbound.RequestByNameReadRepositoryPort;
import dev.ime.infrastructure.entity.GenreJpaEntity;
import io.smallrye.mutiny.converters.uni.UniReactorConverters;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import reactor.core.publisher.Mono;

@Repository
@Qualifier("genreReadRepositoryAdapter")
public class GenreReadRepositoryAdapter implements ReadRepositoryPort<Genre>, RequestByNameReadRepositoryPort {

	private final SessionFactory sessionFactory;
	private final GenreMapper mapper;
	
	public GenreReadRepositoryAdapter(SessionFactory sessionFactory, GenreMapper mapper) {
		super();
		this.sessionFactory = sessionFactory;
		this.mapper = mapper;
	}

	@Override
	public Mono<Page<Genre>> findAll(Pageable pageable) {

		CriteriaBuilder cb = sessionFactory.getCriteriaBuilder();
		CriteriaQuery<GenreJpaEntity> cq = cb.createQuery(GenreJpaEntity.class);
		Root<GenreJpaEntity> root = cq.from(GenreJpaEntity.class);
		
	    var booksFetch = root.fetch("books", JoinType.LEFT);
	    booksFetch.fetch("publisher", JoinType.LEFT);

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
	    Root<GenreJpaEntity> countRoot = countQuery.from(GenreJpaEntity.class);
	    countQuery.select(cb.countDistinct(countRoot));

	    var countUni = sessionFactory.withSession(
	        session -> session.createQuery(countQuery).getSingleResult()
	    );

	    Mono<List<GenreJpaEntity>> entityMono = Mono.from(dataQuery.convert().with(UniReactorConverters.toMono()));
	    Mono<Long> countMono = Mono.from(countUni.convert().with(UniReactorConverters.toMono()));

	    return Mono.zip(entityMono, countMono)
	        .map(tuple -> {
	            List<Genre> domList = convertToDom(tuple.getT1());
	            long total = tuple.getT2();
	            return convertToPage(domList, pageable, total);
	        });
	}

	private List<Genre> convertToDom(List<GenreJpaEntity> list) {
		
		return list.stream()
				.map(mapper::fromJpaToDomain)
				.toList();
	}
	
	private Page<Genre> convertToPage(List<Genre> list, Pageable pageable, long total) {
		
		return new PageImpl<>(list, pageable, total);
	}
	
	private List<Order> buildSortOrder(Sort sort, CriteriaBuilder criteriaBuilder, Root<GenreJpaEntity> root) {
	
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
	public Mono<Genre> findById(UUID id) {

		String queryString = """
				SELECT
				 e
				FROM
				 GenreJpaEntity e
				LEFT JOIN FETCH e.books b
				LEFT JOIN FETCH b.publisher
				WHERE genreId = ?1
				""";

		return sessionFactory
				.withSession(session -> session.createQuery(queryString, GenreJpaEntity.class)
						.setParameter(1, id)
						.getSingleResultOrNull())
				.convert().with(UniReactorConverters.toMono())
				.map(mapper::fromJpaToDomain);
	}

	@Override
	public Mono<Boolean> existsByName(String name) {

		String jpql = "SELECT COUNT(b) FROM GenreJpaEntity b WHERE b.name = :name";
		return sessionFactory
				.withSession(
						session -> session.createQuery(jpql, Long.class).setParameter(GlobalConstants.MODEL_NAME, name)
								.getSingleResult().map(count -> count != null && count > 0))
				.convert().with(UniReactorConverters.toMono());
	}

	@Override
	public Mono<Boolean> findByNameAndIdNot(String name, UUID id) {

		CriteriaBuilder cb = sessionFactory.getCriteriaBuilder();
		CriteriaQuery<GenreJpaEntity> cq =  cb.createQuery(GenreJpaEntity.class);
		Root<GenreJpaEntity> root = cq.from(GenreJpaEntity.class);
	    Predicate conditionName = cb.equal(root.get(GlobalConstants.MODEL_NAME), name);
	    Predicate conditionId = cb.notEqual(root.get(GlobalConstants.GENRE_ID), id);
	    cq.where(cb.and(conditionName, conditionId));
	    
		return sessionFactory.withSession(session -> session.createQuery(cq)
				.getResultCount().map( count -> count > 0))
				.convert().with(UniReactorConverters.toMono());	
	}
}
