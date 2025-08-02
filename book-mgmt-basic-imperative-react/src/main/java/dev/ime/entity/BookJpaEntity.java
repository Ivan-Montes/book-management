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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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
@Table ( name = GlobalConstants.BOOK_CAT_DB)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Generated
public class BookJpaEntity {

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY)
	@Column( name = GlobalConstants.BOOK_ID_DB)
	private Long bookId;
	
	@Column(unique = true, nullable = false, length = 13, name = GlobalConstants.BOOK_ISBN_DB)
	private String isbn;
	
	@Column(nullable = false, length = 100, name = GlobalConstants.BOOK_TITLE_DB)
	private String title;		

	@CreationTimestamp
	@Column( name = GlobalConstants.MODEL_CREATIONTIME_DB)
	private LocalDateTime creationTimestamp;
	
	@UpdateTimestamp
	@Column( name = GlobalConstants.MODEL_UPDATETIME_DB)
	private LocalDateTime updateTimestamp;
	
	@ManyToOne
	@JoinColumn( name = GlobalConstants.PUBLI_ID_DB)
	private PublisherJpaEntity publisher;
	
	@ManyToOne
	@JoinColumn( name = GlobalConstants.GENRE_ID_DB)
	private GenreJpaEntity genre;
	
	@ManyToMany
	@JoinTable(
			name = "BOOKS_AUTHORS",
			joinColumns = @JoinColumn( name = GlobalConstants.BOOK_ID_DB),
			inverseJoinColumns = @JoinColumn( name = GlobalConstants.AUTHOR_ID_DB)
			)
	@ToString.Exclude
	@Builder.Default
	private Set<AuthorJpaEntity>authors = new HashSet<>();
	
	@OneToMany( mappedBy = "book")
	@ToString.Exclude
	@Builder.Default
	private Set<BookBookshopJpaEntity> bookshops = new HashSet<>();	
}
