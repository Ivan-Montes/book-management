import { useState, useEffect } from 'react';
import PropTypes from 'prop-types';

function PublisherModal({ selectedPublisher, onSelect, onClose }) {
  const [page, setPage] = useState(0);
  const [publishers, setPublishers] = useState([]);
  const [totalPages, setTotalPages] = useState(0);
  const [error, setError] = useState(null);
  const [tempSelected, setTempSelected] = useState(selectedPublisher || null);
  const size = 10;

  const loadPublishers = async (page = 0) => {
    try {
      const res = await fetch(`/publishers?page=${page}&size=${size}`);
      if (!res.ok) {
        const errorData = await res.json();
        throw new Error(errorData.message || 'Error fetching publishers');
      }
      const data = await res.json();
      setPublishers(data.content);
      setTotalPages(data.totalPages);
      setError(null);
    } catch (err) {
      setError(err.message);
    }
  };

  useEffect(() => {
    loadPublishers(page);
  }, [page]);

  const handleConfirm = () => {
    if (tempSelected) {
      onSelect(tempSelected);
      onClose();
    } else {
      alert('Please select a publisher.');
    }
  };

  return (
    <div
      className="modal"
      role="dialog"
      aria-modal="true"
      tabIndex="-1"
      style={{ display: 'block', backgroundColor: 'rgba(0,0,0,0.5)' }}
    >
      <div className="modal-dialog">
        <div className="modal-content">

          <div className="modal-header">
            <h5 className="modal-title">Select Publisher</h5>
            <button type="button" className="btn-close" onClick={onClose} />
          </div>

          <div className="modal-body">
            {error && <div className="alert alert-danger">{error}</div>}
            <ul className="list-group">
              {publishers.map(pub => (
                <li
                  key={pub.publisherId}
                  className={`list-group-item ${
                    tempSelected?.publisherId === pub.publisherId ? 'active' : ''
                  }`}
                  style={{ cursor: 'pointer' }}
                  onClick={() => setTempSelected(pub)}
                >
                  <strong>{pub.name}</strong>
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

PublisherModal.propTypes = {
  selectedPublisher: PropTypes.shape({
    publisherId: PropTypes.number,
    name: PropTypes.string,
  }),
  onSelect: PropTypes.func.isRequired,
  onClose: PropTypes.func.isRequired,
};

export default PublisherModal;
