package dev.ime.utils;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import dev.ime.common.GlobalConstants;
import dev.ime.dto.PaginatedResponse;
import dev.ime.dto.PaginationDto;
import dev.ime.model.PaginatedResult;

@Component
public class PaginationUtils {
	
	public PaginationDto createPaginationDto(
	        Integer pageParam,
	        Integer sizeParam,
	        String sortByParam,
	        String sortDirParam,
	        Set<String> allowedSortAttrs,
	        String defaultSortBy
	    ) {
	        Integer page = Optional.ofNullable(pageParam).filter(i -> i >= 0).orElse(0);
	        Integer size = Optional.ofNullable(sizeParam).filter(i -> i > 0).orElse(GlobalConstants.PAGE_SIZE_DEFAULT);
	        String sortBy = Optional.ofNullable(sortByParam).filter(allowedSortAttrs::contains).orElse(defaultSortBy);
	        String sortDir = Optional.ofNullable(sortDirParam)
	            .map(String::toUpperCase)
	            .filter(dir -> dir.equals(GlobalConstants.DIR_A) || dir.equals(GlobalConstants.DIR_D))
	            .orElse(GlobalConstants.DIR_A);

	        return new PaginationDto(page, size, sortBy, sortDir);
	    }
	
    public PageRequest createPageRequest(PaginationDto dto) {
        Sort sort = Sort.by(Sort.Direction.fromString(dto.sortDir()), dto.sortBy());
        return PageRequest.of(dto.page(), dto.size(), sort);
    }    

    public <T>PaginatedResult<T> createPaginatedResponse(Page<T> page) {
	    return new PaginatedResponse<>(
	            page.getContent(),
	            page.getNumber(),
	            page.getSize(),
	            page.getTotalElements(),
	            page.getTotalPages(),
	            page.isLast()
	        );
	}	
}
