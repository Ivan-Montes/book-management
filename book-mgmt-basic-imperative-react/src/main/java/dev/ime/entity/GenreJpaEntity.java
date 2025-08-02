package dev.ime.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import dev.ime.common.GlobalConstants;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = GlobalConstants.GENRE_CAT_DB)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Generated
public class GenreJpaEntity {
	
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY)
	@Column( name = GlobalConstants.GENRE_ID_DB)
	private Long genreId;
	
	@Column(unique = true, nullable = false, length = 50, name = GlobalConstants.GENRE_NAME_DB)
	private String name;
	
	@Column(nullable = false, length = 100, name = GlobalConstants.GENRE_DESC_DB)
	private String description;
	
	@CreationTimestamp
	@Column( name = GlobalConstants.MODEL_CREATIONTIME_DB)
	private LocalDateTime creationTimestamp;	

	@UpdateTimestamp
	@Column( name = GlobalConstants.MODEL_UPDATETIME_DB)
	private LocalDateTime updateTimestamp;	
	
	@OneToMany(mappedBy = "genre", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	@Builder.Default
	private Set<BookJpaEntity>books = new HashSet<>();
}
