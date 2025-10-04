package dev.ime.application.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import dev.ime.application.dto.PaginatedDto;
import dev.ime.application.dto.PaginationDto;
import dev.ime.common.constants.GlobalConstants;
import dev.ime.domain.model.PaginatedResult;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PaginationUtils {

	private final SortingValidationUtils sortingValidationUtils;

	public Mono<PaginationDto> createPaginationDto(ServerRequest serverRequest, Class<?> dtoClass) {
    	
    	return Mono.fromCallable( () -> {
    		
    		Integer page = serverRequest.queryParam(GlobalConstants.PS_PAGE).map(Integer::parseInt).orElse(0);
        	Integer size = serverRequest.queryParam(GlobalConstants.PS_SIZE).map(Integer::parseInt).orElse(GlobalConstants.PAGE_SIZE_DEFAULT);
            String sortBy = serverRequest.queryParam(GlobalConstants.PS_BY)
                    .filter( sortField -> sortingValidationUtils.isValidSortField(dtoClass, sortField))
                    .orElseGet( () -> sortingValidationUtils.getDefaultSortField(dtoClass));
            String sortDir = serverRequest.queryParam(GlobalConstants.PS_DIR)
            		.map(String::toUpperCase)
            		.filter( sorting -> sorting.equals(GlobalConstants.DIR_A) || sorting.equals(GlobalConstants.DIR_D))
            		.orElse(GlobalConstants.DIR_A);
            
            return new PaginationDto(page, size, sortBy, sortDir);
            
    	}).onErrorMap( e -> new IllegalArgumentException(GlobalConstants.MSG_PAGED_FAIL, e));    	
        
    }
    public PageRequest createPageRequest(PaginationDto dto) {
        Sort sort = Sort.by(Sort.Direction.fromString(dto.sortDir()), dto.sortBy());
        return PageRequest.of(dto.page(), dto.size(), sort);
    }    

    public <T>PaginatedResult<T> createPaginatedResponse(Page<T> page) {
	    return new PaginatedDto<>(
	            page.getContent(),
	            page.getNumber(),
	            page.getSize(),
	            page.getTotalElements(),
	            page.getTotalPages(),
	            page.isLast()
	        );
	}
}
