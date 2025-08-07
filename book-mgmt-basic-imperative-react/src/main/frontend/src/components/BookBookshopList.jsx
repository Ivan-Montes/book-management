import PropTypes from 'prop-types';
import { Link } from 'react-router-dom';

function BookBookshopList({ paginatedResult, onEdit, onDelete, onPageChange }) {
  const { content, page, totalPages } = paginatedResult;

  return (
    <div className="container">
      <div className="table-responsive">
        <h2 className="text-center">Books in Bookshops</h2>
        <table className="table table-striped table-hover table-bordered">
          <thead>
            <tr>
              <th>Book ID</th>
              <th>ISBN</th>
              <th>Title</th>
              <th>Bookshop ID</th>
              <th>Bookshop Name</th>
              <th>Price</th>
              <th>Units</th>
              <th>Edit</th>
              <th>Delete</th>
            </tr>
          </thead>
          <tbody>
            {content.map(item => (
              <tr key={`${item.bookId}-${item.bookshopId}`}>
                <td>{item.bookId}</td>
                <td>{item.isbn}</td>
                <td>{item.title}</td>
                <td>{item.bookshopId}</td>
                <td>{item.name}</td>
                <td>{item.price}</td>
                <td>{item.units}</td>
                <td>
                  <button
                    type="button"
                    className="btn btn-sm btn-info"
                    onClick={() => onEdit(item.bookId, item.bookshopId)}
                  >
                    Edit
                  </button>
                </td>
                <td>
                  <button
                    type="button"
                    className="btn btn-sm btn-danger"
                    onClick={() => onDelete(item.bookId, item.bookshopId)}
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
        <Link to="/add-bookbookshop" className="btn btn-outline-success">
          Add New
        </Link>
      </div>
    </div>
  );
}

BookBookshopList.propTypes = {
  paginatedResult: PropTypes.shape({
    content: PropTypes.arrayOf(
      PropTypes.shape({
        bookId: PropTypes.number.isRequired,
        isbn: PropTypes.string.isRequired,
        title: PropTypes.string.isRequired,
        bookshopId: PropTypes.number.isRequired,
        name: PropTypes.string.isRequired,
        price: PropTypes.number.isRequired,
        units: PropTypes.number.isRequired,
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

export default BookBookshopList;
