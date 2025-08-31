package dev.ime.infrastructure.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import dev.ime.common.constants.GlobalConstants;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = GlobalConstants.PUBLI_CAT_DB)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class PublisherJpaEntity {

	@Id
	@Column(name = GlobalConstants.PUBLI_ID_DB)
	private UUID publisherId;

	@Column(unique = true, nullable = false, length = 50, name = GlobalConstants.PUBLI_NAME_DB)
	private String name;

	@CreationTimestamp
	@Column(name = GlobalConstants.MODEL_CREATIONTIME_DB)
	private LocalDateTime creationTimestamp;

	@UpdateTimestamp
	@Column(name = GlobalConstants.MODEL_UPDATETIME_DB)
	private LocalDateTime updateTimestamp;

	@OneToMany(mappedBy = "publisher", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	@Builder.Default
	private Set<BookJpaEntity> books = new HashSet<>();
}
