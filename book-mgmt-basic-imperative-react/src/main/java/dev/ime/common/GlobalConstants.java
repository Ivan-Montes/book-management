package dev.ime.common;

public final class GlobalConstants {

	private GlobalConstants() {
		super();
	}

	// Messages
	public static final String MSG_PATTERN_SEVERE = "### [** eXception **] -> [{}] : [{}] ###";
	public static final String MSG_PATTERN_INFO = "### [{}] : [{}] ###";
	public static final String MSG_NODATA = "No data available";
	public static final String MSG_EMPTY = "Empty";
	public static final String MSG_COLLECT = "Java Collection";

	// Models
	public static final String GENRE_CAT = "Genre";
	public static final String GENRE_CAT_DB = "genres";
	public static final String GENRE_ID = "genreId";
	public static final String GENRE_ID_DB = "genre_id";
	public static final String GENRE_NAME = "genreName";
	public static final String GENRE_NAME_DB = "genre_name";
	public static final String GENRE_DESC = "genreDescription";
	public static final String GENRE_DESC_DB = "genre_description";
	public static final String PUBLI_CAT = "Publisher";
	public static final String PUBLI_CAT_DB = "publishers";
	public static final String PUBLI_ID = "publisherId";
	public static final String PUBLI_ID_DB = "publisher_id";
	public static final String PUBLI_NAME = "publisherName";
	public static final String PUBLI_NAME_DB = "publisher_name";
	public static final String AUTHOR_CAT = "Author";
	public static final String AUTHOR_CAT_DB = "authors";
	public static final String AUTHOR_ID = "authorId";
	public static final String AUTHOR_ID_DB = "author_id";
	public static final String AUTHOR_NAME = "authorName";
	public static final String AUTHOR_NAME_DB = "author_name";
	public static final String AUTHOR_SUR= "authorSurname";
	public static final String AUTHOR_SUR_DB = "author_surname";
	public static final String BS_CAT = "Bookshop";
	public static final String BS_CAT_DB = "bookshops";
	public static final String BS_ID = "bookshopId";
	public static final String BS_ID_DB = "bookshop_id";
	public static final String BS_NAME = "bookshopName";
	public static final String BS_NAME_DB = "bookshop_name";
	public static final String BBS_CAT = "BookBookshop";
	public static final String BBS_ID = "bookBookshopId";
	public static final String BBS_CAT_DB = "books_bookshops";
	public static final String BBS_PRICE = "price";
	public static final String BBS_PRICE_DB = "price";
	public static final String BBS_UNITS = "units";
	public static final String BBS_UNITS_DB = "units";
	public static final String BOOK_CAT = "Book";
	public static final String BOOK_CAT_DB = "books";
	public static final String BOOK_ID = "bookId";
	public static final String BOOK_ID_DB = "book_id";
	public static final String BOOK_ISBN = "isbn";
	public static final String BOOK_ISBN_DB = "book_isbn";
	public static final String BOOK_TITLE = "title";
	public static final String BOOK_TITLE_DB = "book_title";
	
	public static final String MODEL_ID = "id";
	public static final String MODEL_NAME = "name";
	public static final String MODEL_DESC = "description";
	public static final String MODEL_CREATIONTIME_DB = "creation_timestamp";
	public static final String MODEL_UPDATETIME_DB = "update_timestamp";	
	
	// Exceptions
	public static final String EX_BASIC = "BasicException";
	public static final String EX_PLAIN = "Exception";
	public static final String EX_PLAIN_DESC = "Exception friendly";
	public static final String EX_RESOURCENOTFOUND = "ResourceNotFoundException";
	public static final String EX_RESOURCENOTFOUND_DESC = "The resource has not been found.";
	public static final String EX_DUP = "DuplicatedEntityException";
	public static final String EX_DUP_DESC = "Duplicated Entity in DB";
	public static final String EX_ARGNOVALID = "MethodArgumentNotValidException";
	public static final String EX_ARGMISMATCH = "MethodArgumentTypeMismatchException";
	public static final String EX_CONSTRAINT = "ConstraintViolationException";
	public static final String EX_DATAINTEGRITY = "DataIntegrityViolationException";

	// Pagination
	public static final Integer PAGE_SIZE_DEFAULT = 33;
	public static final String DIR_A = "ASC";
	public static final String DIR_D = "DESC";
}
