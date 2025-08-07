import PropTypes from 'prop-types';
import { Link } from 'react-router-dom';

function BookList({ paginatedResult, onEdit, onDelete, onPageChange }) {
  const { content, page, totalPages } = paginatedResult;
  return (
    <div className="container">
      <div className="table-responsive">
        <h2 className="text-center">List of Books</h2>
        <table className="table table-striped table-hover table-bordered">
          <thead>
            <tr>
              <th>ID</th>
              <th>ISBN</th>
              <th>Title</th>
			  <th>Authors</th>
              <th>Genre</th>
			  <th>Publisher</th>
              <th>Edit</th>
              <th>Delete</th>
            </tr>
          </thead>
          <tbody>
            {content.map((book) => (
              <tr key={book.bookId}>
                <td>{book.bookId}</td>
                <td>{book.isbn}</td>
                <td>{book.title}</td>
				<td>
				  {book.authorsSet && book.authorsSet.length > 0
				    ? Array.from(book.authorsSet)
				        .map(author => (
				          <p key={author.authorId}>
				            {author.name} {author.surname}
				          </p>
				        ))
				    : <p>No authors</p>}
				</td>
                <td>{book.genreName}</td>
				<td>{book.publisherName}</td>
                <td>
                  <button
                    type="button"
                    className="btn btn-sm btn-info"
                    onClick={() => onEdit(book.bookId)}
                  >
                    Edit
                  </button>
                </td>
                <td>
                  <button
                    type="button"
                    className="btn btn-sm btn-danger"
                    onClick={() => onDelete(book.bookId)}
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      <div className="d-flex justify-content-center mt-3">
        <button
          className="btn btn-outline-primary me-2"
          disabled={page === 0}
          onClick={() => onPageChange(page - 1)}
        >
          Previous
        </button>
        <span className="mx-2">
          Page {page + 1} of {totalPages}
        </span>
        <button
          className="btn btn-outline-primary ms-2"
          disabled={page >= totalPages - 1}
          onClick={() => onPageChange(page + 1)}
        >
          Next
        </button>
      </div>
      <div className="text-center mt-3">
        <Link to="/add-book" className="btn btn-outline-success">
          Add New
        </Link>
      </div>
    </div>
  );
}

BookList.propTypes = {
  paginatedResult: PropTypes.shape({
    content: PropTypes.arrayOf(
      PropTypes.shape({
        bookId: PropTypes.number.isRequired,
        isbn: PropTypes.string.isRequired,
        title: PropTypes.string.isRequired,
        publisherId: PropTypes.number.isRequired,
		publisherName: PropTypes.string.isRequired,
        genreId: PropTypes.number.isRequired,
		genreName: PropTypes.string.isRequired,
		authorsSet: PropTypes.instanceOf(Set).isRequired,
      })
    ).isRequired,
    page: PropTypes.number.isRequired,
    size: PropTypes.number.isRequired,
    totalElements: PropTypes.number.isRequired,
    totalPages: PropTypes.number.isRequired,
    last: PropTypes.bool.isRequired,
  }).isRequired,
  onEdit: PropTypes.func.isRequired,
  onDelete: PropTypes.func.isRequired,
  onPageChange: PropTypes.func.isRequired,
};

export default BookList;
