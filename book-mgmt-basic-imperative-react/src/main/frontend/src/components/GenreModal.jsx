import { useState, useEffect } from 'react';
import PropTypes from 'prop-types';

function GenreModal({ selectedGenre, onSelect, onClose }) {
  const [page, setPage] = useState(0);
  const [genres, setGenres] = useState([]);
  const [totalPages, setTotalPages] = useState(0);
  const [error, setError] = useState(null);
  const [tempSelected, setTempSelected] = useState(selectedGenre || null);
  const size = 10;

  const loadGenres = async (page = 0) => {
    try {
      const res = await fetch(`/genres?page=${page}&size=${size}`);
      if (!res.ok) {
        const errorData = await res.json();
        throw new Error(errorData.message || 'Error fetching genres');
      }
      const data = await res.json();
      setGenres(data.content);
      setTotalPages(data.totalPages);
      setError(null);
    } catch (err) {
      setError(err.message);
    }
  };

  useEffect(() => {
    loadGenres(page);
  }, [page]);

  const handleConfirm = () => {
    if (tempSelected) {
      onSelect(tempSelected);
      onClose();
    } else {
      alert('Please select a genre.');
    }
  };

  return (
    <div
      className="modal"
      style={{ display: 'block', backgroundColor: 'rgba(0,0,0,0.5)' }}
      role="dialog"
      aria-modal="true"
      tabIndex="-1"
    >
      <div className="modal-dialog">
        <div className="modal-content">

          <div className="modal-header">
            <h5 className="modal-title">Select Genre</h5>
            <button type="button" className="btn-close" onClick={onClose} />
          </div>

          <div className="modal-body">
            {error && <div className="alert alert-danger">{error}</div>}
            <ul className="list-group">
              {genres.map(genre => (
                <li
                  key={genre.genreId}
                  className={`list-group-item ${
                    tempSelected?.genreId === genre.genreId ? 'active' : ''
                  }`}
                  style={{ cursor: 'pointer' }}
                  onClick={() => setTempSelected(genre)}
                >
                  <div className="d-flex flex-column">
                    <strong>{genre.name}</strong>
                    <small className="text-muted">{genre.description}</small>
                  </div>
                </li>
              ))}
            </ul>
          </div>

          <div className="modal-footer">
            <button
              className="btn btn-outline-primary me-2"
              disabled={page === 0}
              onClick={() => setPage(page - 1)}
            >
              Previous
            </button>
            <span>Page {page + 1} of {totalPages}</span>
            <button
              className="btn btn-outline-primary ms-2"
              disabled={page >= totalPages - 1}
              onClick={() => setPage(page + 1)}
            >
              Next
            </button>
            <button className="btn btn-primary" onClick={handleConfirm}>
              Confirm
            </button>
            <button className="btn btn-secondary" onClick={onClose}>
              Cancel
            </button>
          </div>

        </div>
      </div>
    </div>
  );
}

GenreModal.propTypes = {
  selectedGenre: PropTypes.shape({
    genreId: PropTypes.number,
    name: PropTypes.string,
    description: PropTypes.string,
  }),
  onSelect: PropTypes.func.isRequired,
  onClose: PropTypes.func.isRequired,
};

export default GenreModal;
