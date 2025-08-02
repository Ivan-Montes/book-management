package dev.ime.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import dev.ime.common.GlobalConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table( name = GlobalConstants.AUTHOR_CAT_DB)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Generated
public class AuthorJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column( name = GlobalConstants.AUTHOR_ID_DB)
	private Long authorId;
	 
	@Column(nullable = false, length = 50, name = GlobalConstants.AUTHOR_NAME_DB)
	private String name;
	
	@Column(nullable=false, length = 50, name = GlobalConstants.AUTHOR_SUR_DB)
	private String surname;
	
	@CreationTimestamp
	@Column( name = GlobalConstants.MODEL_CREATIONTIME_DB)
	private LocalDateTime creationTimestamp;
	
	@UpdateTimestamp
	@Column( name = GlobalConstants.MODEL_UPDATETIME_DB)
	private LocalDateTime updateTimestamp;
	
	@ManyToMany( mappedBy = "authors")
	@ToString.Exclude
	@Builder.Default
	private Set<BookJpaEntity>books = new HashSet<>();	
}
