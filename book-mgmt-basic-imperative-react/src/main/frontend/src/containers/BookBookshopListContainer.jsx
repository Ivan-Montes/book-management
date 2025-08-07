import { useEffect, useState } from 'react';
import BookBookshopList from '../components/BookBookshopList';
import { useNavigate } from 'react-router-dom';
import ConfirmModal from '../components/ConfirmModal';

function BookBookshopListContainer() {
  const [error, setError] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [itemToDelete, setItemToDelete] = useState(null);
  const navigate = useNavigate();

  const [paginatedResult, setPaginatedResult] = useState({
    content: [],
    page: 0,
    size: 10,
    totalElements: 0,
    totalPages: 0,
    last: true,
  });

  const loadBookBookshops = async (page = 0, size = 10) => {
    try {
      const res = await fetch(`/bookbookshops?page=${page}&size=${size}`);
      if (!res.ok) {
        const errorData = await res.json();
        throw new Error(errorData.message || 'Error fetching data');
      }
      const data = await res.json();
      setPaginatedResult(data);
      setError(null);
    } catch (err) {
      setError(err.message);
    }
  };

  useEffect(() => {
    loadBookBookshops(0, paginatedResult.size);
  }, []);

  const handleEdit = (bookId, bookshopId) => {
    navigate(`/edit-bookbookshop/book/${bookId}/bookshop/${bookshopId}`);
  };

  const handleDelete = (bookId, bookshopId) => {
    const item = paginatedResult.content.find(i => i.bookId === bookId && i.bookshopId === bookshopId);
    setItemToDelete(item);
    setShowModal(true);
  };

  const handlePageChange = (newPage) => {
    loadBookBookshops(newPage, paginatedResult.size);
  };

  const confirmDelete = () => {
    const { bookId, bookshopId } = itemToDelete;

    fetch(`/bookbookshops/book/${bookId}/bookshop/${bookshopId}`, {
      method: 'DELETE',
    })
      .then(res => {
        if (!res.ok) {
          return res.json().then(errorData => {
            throw new Error(`${errorData.name || 'Error'}: ${errorData.description || ''}`);
          });
        }
        loadBookBookshops(paginatedResult.page, paginatedResult.size);
        setShowModal(false);
      })
      .catch(err => {
        alert(err.message);
        setShowModal(false);
      });
  };

  return (
    <div>
      {error && <div className="alert alert-danger">{error}</div>}
      <BookBookshopList
        paginatedResult={paginatedResult}
        onEdit={handleEdit}
        onDelete={handleDelete}
        onPageChange={handlePageChange}
      />
      <ConfirmModal
        show={showModal}
        title="Confirm Deletion"
        message={`Are you sure you want to delete the book "${itemToDelete?.title}" from bookshop "${itemToDelete?.name}"?`}
        onConfirm={confirmDelete}
        onCancel={() => setShowModal(false)}
      />
    </div>
  );
}

export default BookBookshopListContainer;
