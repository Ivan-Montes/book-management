package dev.ime.common.mapper;

import java.util.List;


public interface GenericMapper<D, M, J> {

	M fromDtoToDomain(D dto);
	J fromDomainToJpa(M dom);
	M fromJpaToDomain(J jpa);
	List<M> fromListJpaToListDomain(List<J> list);
	D fromDomainToDto(M dom);
	List<D> fromListDomainToListDto(List<M> list);	
}
