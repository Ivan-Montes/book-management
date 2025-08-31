package dev.ime.infrastructure.entity;

import java.io.Serializable;
import java.util.UUID;

import dev.ime.common.constants.GlobalConstants;
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
public class BookBookshopId implements Serializable {
	
	private static final long serialVersionUID = 7292169197803696894L;

	@Column(name = GlobalConstants.BOOK_ID_DB)
	private UUID bookId;

	@Column(name = GlobalConstants.BS_ID_DB)
	private UUID bookshopId;
}
