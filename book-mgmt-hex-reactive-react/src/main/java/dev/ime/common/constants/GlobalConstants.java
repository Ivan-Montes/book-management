package dev.ime.common.constants;

public final class GlobalConstants {

	private GlobalConstants() {
		super();
	}

	// Patterns
	public static final String PATTERN_ID = "^[\\d]+$";
	public static final String PATTERN_ISBN = "^[\\d]{10,13}$";
	public static final String PATTERN_TITLE_FULL = "[a-zA-Z0-9ñÑáéíóúÁÉÍÓÚ\\s\\-\\?¿\\!¡\\.&,:]{1,50}";
	public static final String PATTERN_NAME_FULL = "^[a-zA-Z0-9ñÑáéíóúÁÉÍÓÚ][a-zA-Z0-9ñÑáéíóúÁÉÍÓÚ\\s\\-\\.&,:]{1,49}$";
	public static final String PATTERN_SURNAME_FULL = "^[a-zA-Z0-9ñÑáéíóúÁÉÍÓÚ][a-zA-Z0-9ñÑáéíóúÁÉÍÓÚ\\s\\-\\.&,:]{1,49}$";
	public static final String PATTERN_DESC_FULL = "^[a-zA-Z0-9ñÑáéíóúÁÉÍÓÚ][a-zA-Z0-9ñÑáéíóúÁÉÍÓÚ\\s\\-\\.&,:]{1,99}$";
	public static final String PATTERN_EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
	public static final String PATTERN_PRICE = "^(?:\\d{1,3})(?:\\.\\d+)?$";
	public static final String PATTERN_UNITS = "^[\\d]{1,2}$";

	// Models
	public static final String AUTHOR_CAT = "Author";
	public static final String AUTHOR_CAT_DB = "authors";
	public static final String AUTHOR_ID = "authorId";
	public static final String AUTHOR_ID_DB = "author_id";
	public static final String AUTHOR_NAME = "authorName";
	public static final String AUTHOR_NAME_DB = "author_name";
	public static final String AUTHOR_SUR = "authorSurname";
	public static final String AUTHOR_SUR_DB = "author_surname";
	public static final String AUTHOR_IDSET = "authorIdSet";
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

	// Model Generic
	public static final String MODEL_ID = "id";
	public static final String MODEL_NAME = "name";
	public static final String MODEL_SURNAME = "surname";
	public static final String MODEL_DESC = "description";
	public static final String MODEL_CREATIONTIME_DB = "creation_timestamp";
	public static final String MODEL_UPDATETIME_DB = "update_timestamp";

	//Events
	public static final String EVENT_CAT = "Event";
	public static final String EVENT_DB = "events";
	public static final String EVENT_ID = "eventId";
	public static final String EVENT_ID_DB = "event_id";
	public static final String EVENT_CATEGORY_DB = "event_category";
	public static final String EVENT_TYPE_DB = "event_type";
	public static final String EVENT_TIMESTAMP_DB = "event_timestamp";
	public static final String EVENT_DATA_DB = "event_data";

	// Orders
	public static final String CREATE_AUTHOR = "create.author";
	public static final String UPDATE_AUTHOR = "update.author";
	public static final String DELETE_AUTHOR = "delete.author";
	public static final String CREATE_BS = "create.bookshop";
	public static final String UPDATE_BS = "update.bookshop";
	public static final String DELETE_BS = "delete.bookshop";
	public static final String CREATE_BBS = "create.bookbookshop";
	public static final String UPDATE_BBS = "update.bookbookshop";
	public static final String DELETE_BBS = "delete.bookbookshop";
	public static final String CREATE_BOOK = "create.book";
	public static final String UPDATE_BOOK = "update.book";
	public static final String DELETE_BOOK = "delete.book";
	public static final String CREATE_GENRE = "create.genre";
	public static final String UPDATE_GENRE = "update.genre";
	public static final String DELETE_GENRE = "delete.genre";
	public static final String CREATE_PUBLI = "create.publisher";
	public static final String UPDATE_PUBLI = "update.publisher";
	public static final String DELETE_PUBLI = "delete.publisher";

	// Topics
	public static final String AUTHOR_CREATED = "author.created";
	public static final String AUTHOR_UPDATED = "author.updated";
	public static final String AUTHOR_DELETED = "author.deleted";
	public static final String BS_CREATED = "bookshop.created";
	public static final String BS_UPDATED = "bookshop.updated";
	public static final String BS_DELETED = "bookshop.deleted";
	public static final String BBS_CREATED = "bookbookshop.created";
	public static final String BBS_UPDATED = "bookbookshop.updated";
	public static final String BBS_DELETED = "bookbookshop.deleted";
	public static final String BOOK_CREATED = "book.created";
	public static final String BOOK_UPDATED = "book.updated";
	public static final String BOOK_DELETED = "book.deleted";
	public static final String GENRE_CREATED = "genre.created";
	public static final String GENRE_UPDATED = "genre.updated";
	public static final String GENRE_DELETED = "genre.deleted";
	public static final String PUBLI_CREATED = "publisher.created";
	public static final String PUBLI_UPDATED = "publisher.updated";
	public static final String PUBLI_DELETED = "publisher.deleted";
	
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
	public static final String EX_VALIDATION = "ValidationException";
	public static final String EX_VALIDATION_DESC = "Kernel Panic in validation process";
	public static final String EX_ILLEGALHANDLER = "IllegalHandlerException";
	public static final String EX_ILLEGALHANDLER_DESC = "Illegal Handler in dipatcher Exception";
	public static final String EX_UNIQUEVALUE = "UniqueValueException";
	public static final String EX_UNIQUEVALUE_DESC = "Unique Value constraint infringed";
	public static final String EX_ENTITYASSOCIATED = "EntityAssociatedException";
	public static final String EX_ENTITYASSOCIATED_DESC = "Some entity is still associated in the element";
	public static final String EX_EVENTUNEXPEC = "Event Unexpected Exception";
	public static final String EX_EVENTUNEXPEC_DESC = "Event Unexpected Exception";	
	public static final String EX_PUBLISHEVENT = "PublishEventException";
	public static final String EX_PUBLISHEVENT_DESC = "Failed to publish event";
	public static final String EX_CREATEJPAENTITY = "CreateJpaEntityException";
	public static final String EX_CREATEJPAENTITY_DESC = "Exception while creation a JPA entity for saving to sql db";
	public static final String EX_EMPTYRESPONSE = "EmptyResponseException";
	public static final String EX_EMPTYRESPONSE_DESC = "No freak out, just an Empty Response";
	public static final String EX_CREATEEVENT = "CreateEventException";
	public static final String EX_CREATEEVENT_DESC = "Exception while creation a Event";
	

	// Messages
	public static final String MSG_PATTERN_SEVERE = "### [** eXception **] -> [{}] : [{}] ###";
	public static final String MSG_PATTERN_INFO = "### [{}] : [{}] ###";
	public static final String MSG_NODATA = "No data available";
	public static final String MSG_EMPTY = "Empty";
	public static final String MSG_COLLECT = "Java Collection";	
	public static final String MSG_REQUIRED = " *REQUIRED* ";

	public static final String MSG_PUBLISH_EVENT = "Publishing Event";	
	public static final String MSG_PUBLISH_OK = "Publish Event Succesfully";	
	public static final String MSG_PUBLISH_FAIL = "Publish Event Failed";	
	public static final String MSG_PUBLISH_END = "End Publish Event";	
	public static final String MSG_SUBS_RECEIVED = "Subscribed Event received";			
	
	public static final String MSG_FLOW_SUBS = "Subscribed to flow request";
	public static final String MSG_FLOW_PROCESS = "Processing reactive flow";
	public static final String MSG_FLOW_OK = "Reactive flow processed succesfully";
	public static final String MSG_FLOW_CANCEL = "Cancellation occurred during processing";
	public static final String MSG_FLOW_ERROR = "Error processing reactive flow";
	public static final String MSG_FLOW_RESULT = "Reactive flow result";
	public static final String MSG_MODLINES = "Modificated lines: ";
	
	// Pagination
	public static final Integer PAGE_SIZE_DEFAULT = 33;
	public static final String DIR_A = "ASC";
	public static final String DIR_D = "DESC";
	
	// Others
	public static final String OBJ_FIELD = "field";
	public static final String OBJ_VALUE = "value";
	public static final String OBJ_R = "reason";
}
