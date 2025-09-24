package dev.ime.infrastructure.entity;

import dev.ime.common.constants.GlobalConstants;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = GlobalConstants.BBS_CAT_DB)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class BookBookshopJpaEntity {

	@EmbeddedId
	private BookBookshopId bookBookshopId;

	@ManyToOne
	@MapsId(GlobalConstants.BOOK_ID)
	private BookJpaEntity book;

	@ManyToOne
	@MapsId(GlobalConstants.BS_ID)
	private BookshopJpaEntity bookshop;

	@Column(nullable = false)
	private Double price;

	@Column(nullable = false)
	private Integer units;
}
