package dev.ime.entity;



import java.io.Serializable;

import dev.ime.common.GlobalConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Generated
public class BookBookshopId  implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column( name = GlobalConstants.BOOK_ID_DB)
	private Long bookId;
	
	@Column( name = GlobalConstants.BS_ID_DB)
	private Long bookshopId;
}
