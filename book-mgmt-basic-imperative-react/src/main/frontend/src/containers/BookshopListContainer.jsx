import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import BookshopList from '../components/BookshopList';
import ConfirmModal from '../components/ConfirmModal';

function BookshopListContainer() {
  const [error, setError] = useState(null);
  const navigate = useNavigate();
  const [showModal, setShowModal] = useState(false);
  const [bookshopToDelete, setBookshopToDelete] = useState(null);

  const [paginatedResult, setPaginatedResult] = useState({
    content: [],
    page: 0,
    size: 10,
    totalElements: 0,
    totalPages: 0,
    last: true,
  });

  const loadBookshops = async (page = 0, size = 10) => {
    try {
      const res = await fetch(`/bookshops?page=${page}&size=${size}`);
      if (!res.ok) {
        const errorData = await res.json();
        throw new Error(errorData.message || 'Error fetching bookshops');
      }
      const data = await res.json();
      setPaginatedResult(data);
      setError(null);
    } catch (err) {
      setError(err.message);
    }
  };

  useEffect(() => {
    loadBookshops(0, paginatedResult.size);
  }, []);

  const handleEdit = (id) => {
    navigate(`/edit-bookshop/${id}`);
  };

  const handleDelete = (id) => {
    const bookshop = paginatedResult.content.find(b => b.bookshopId === id);
    setBookshopToDelete(bookshop);
    setShowModal(true);
  };

  const handlePageChange = (newPage) => {
    loadBookshops(newPage, paginatedResult.size);
  };

  const confirmDelete = () => {
    fetch(`/bookshops/${bookshopToDelete.bookshopId}`, { method: 'DELETE' })
      .then(res => {
        if (!res.ok) {
          return res.json().then(errorData => {
            console.error('Backend error:', errorData);
            const alertMessage = `${errorData.name || 'Error'}: ${errorData.description || ''}`;
            throw new Error(alertMessage);
          });
        }
        loadBookshops(paginatedResult.page, paginatedResult.size);
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
      <BookshopList
        paginatedResult={paginatedResult}
        onEdit={handleEdit}
        onDelete={handleDelete}
        onPageChange={handlePageChange}
      />
      <ConfirmModal
        show={showModal}
        title="Confirm Deletion"
        message={`Are you sure you want to delete the bookshop "${bookshopToDelete?.name}"?`}
        onConfirm={confirmDelete}
        onCancel={() => setShowModal(false)}
      />
    </div>
  );
}

export default BookshopListContainer;
