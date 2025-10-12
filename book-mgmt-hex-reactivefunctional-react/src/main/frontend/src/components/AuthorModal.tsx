import { useState, useEffect } from 'react';
import type { Author } from '../types/author';
import type { PaginatedResult } from '../types/pagination';
import type { MultiSelectModalProps } from '../types/modal';

const AuthorModal: React.FC<MultiSelectModalProps<Author>> = ({
  selected,
  onSelect,
  onClose,
}) => {
  const [page, setPage] = useState<number>(0);
  const [authors, setAuthors] = useState<Author[]>([]);
  const [totalPages, setTotalPages] = useState<number>(0);
  const [error, setError] = useState<string | null>(null);
  const [tempSelected, setTempSelected] = useState<Author[]>([...selected]);

  const size = 10;

  const loadAuthors = async (page: number = 0) => {
    try {
      const res = await fetch(`/authors?page=${page}&size=${size}`);
      if (!res.ok) {
        const errorData = await res.json();
        throw new Error(errorData.message || 'Error fetching authors');
      }
      const data: PaginatedResult<Author> = await res.json();
      setAuthors(data.content);
      setTotalPages(data.totalPages);
      setError(null);
    } catch (err) {
      setError((err as Error).message);
    }
  };

  useEffect(() => {
    loadAuthors(page);
  }, [page]);

  const toggleAuthor = (author: Author) => {
    const exists = tempSelected.some((a) => a.authorId === author.authorId);
    if (exists) {
      setTempSelected(tempSelected.filter((a) => a.authorId !== author.authorId));
    } else {
      setTempSelected([...tempSelected, author]);
    }
  };

  const handleConfirm = () => {
    if (tempSelected.length === 0) {
      alert('Please select at least one author.');
      return;
    }
    onSelect(tempSelected);
    onClose();
  };

  return (
    <div
      className="modal"
      role="dialog"
      aria-modal="true"
      tabIndex={-1}
      style={{ display: 'block', backgroundColor: 'rgba(0,0,0,0.5)' }}
    >
      <div className="modal-dialog">
        <div className="modal-content">

          <div className="modal-header">
            <h5 className="modal-title">Select Authors</h5>
            <button type="button" className="btn-close" onClick={onClose} />
          </div>

          <div className="modal-body">
            {error && <div className="alert alert-danger">{error}</div>}
            <ul className="list-group">
              {authors.map((author) => (
                <li key={author.authorId} className="list-group-item">
                  <div className="form-check">
                    <input
                      type="checkbox"
                      className="form-check-input"
                      id={`author-${author.authorId}`}
                      checked={tempSelected.some((a) => a.authorId === author.authorId)}
                      onChange={() => toggleAuthor(author)}
                    />
                    <label className="form-check-label" htmlFor={`author-${author.authorId}`}>
                      <strong>{author.name} {author.surname}</strong>
                    </label>
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
};

export default AuthorModal;
