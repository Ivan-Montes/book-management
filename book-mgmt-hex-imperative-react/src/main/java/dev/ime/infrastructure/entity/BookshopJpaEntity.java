package dev.ime.infrastructure.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import dev.ime.common.constants.GlobalConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = GlobalConstants.BS_CAT_DB)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class BookshopJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = GlobalConstants.BS_ID_DB)
	private Long bookshopId;

	@Column(unique = true, nullable = false, length = 50, name = GlobalConstants.BS_NAME_DB)
	private String name;

	@OneToMany(mappedBy = "bookshop")
	@ToString.Exclude
	@Builder.Default
	private Set<BookBookshopJpaEntity> books = new HashSet<>();

	@CreationTimestamp
	@Column(name = GlobalConstants.MODEL_CREATIONTIME_DB)
	private LocalDateTime creationTimestamp;

	@UpdateTimestamp
	@Column(name = GlobalConstants.MODEL_UPDATETIME_DB)
	private LocalDateTime updateTimestamp;
}
