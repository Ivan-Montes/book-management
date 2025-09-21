import type { Genre } from '../types/genre';
import { Link } from 'react-router-dom';
import type { PaginatedListProps } from '../types/pagination';

type GenreListProps = PaginatedListProps<Genre>;

const GenreList: React.FC<GenreListProps> = ({
  paginatedResult,
  onEdit,
  onDelete,
  onPageChange,
}) => {
  const { content, page, totalPages } = paginatedResult;

  return (
    <div className="container">
      <div className="table-responsive">
        <h2 className="text-center">List of Genres</h2>
        <table className="table table-striped table-hover table-bordered">
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Description</th>
              <th>Edit</th>
              <th>Delete</th>
            </tr>
          </thead>
          <tbody>
            {content.map((genre) => (
              <tr key={genre.genreId}>
                <td>{genre.genreId}</td>
                <td>{genre.name}</td>
                <td>{genre.description}</td>
                <td>
                  <button
                    type="button"
                    className="btn btn-sm btn-info"
                    onClick={() => onEdit(genre.genreId)}
                  >
                    Edit
                  </button>
                </td>
                <td>
                  <button
                    type="button"
                    className="btn btn-sm btn-danger"
                    onClick={() => onDelete(genre.genreId)}
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
        <span className="mx-2">Page {page + 1} of {totalPages}</span>
        <button
          className="btn btn-outline-primary ms-2"
          disabled={page >= totalPages - 1}
          onClick={() => onPageChange(page + 1)}
        >
          Next
        </button>
      </div>
      <div className="text-center mt-3">
        <Link to="/add-genre" className="btn btn-outline-success">Add New</Link>
      </div>
    </div>
  );
};

export default GenreList;
