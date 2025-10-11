import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import BookshopList from '../components/BookshopList';
import ConfirmModal from '../components/ConfirmModal';
import type { Bookshop } from '../types/bookshop';
import type { PaginatedResult } from '../types/pagination';
import SuccessModal from '../components/SuccessModal';

const BookshopListContainer: React.FC = () => {
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();
  const [showModal, setShowModal] = useState(false);
  const [bookshopToDelete, setBookshopToDelete] = useState<Bookshop | null>(null);
  const [showSuccessModal, setShowSuccessModal] = useState(false);
  const [isSuccessModalLoading, setIsSuccessModalLoading] = useState(false);


  const [paginatedResult, setPaginatedResult] = useState<PaginatedResult<Bookshop>>({
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
      const data: PaginatedResult<Bookshop> = await res.json();
      setPaginatedResult(data);
      setError(null);
    } catch (err: unknown) {
      if (err instanceof Error) setError(err.message);
      else setError(String(err));
    }
  };

  useEffect(() => {
    loadBookshops(0, paginatedResult.size);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleEdit = (id: string) => {
    navigate(`/edit-bookshop/${id}`);
  };

  const handleDelete = (id: string) => {
    const bookshop = paginatedResult.content.find(b => b.bookshopId === id) ?? null;
    setBookshopToDelete(bookshop);
    setShowModal(true);
  };

  const handlePageChange = (newPage: number) => {
    loadBookshops(newPage, paginatedResult.size);
  };

  const confirmDelete = async () => {
    if (!bookshopToDelete) return;

    try {
      const res = await fetch(`/bookshops/${bookshopToDelete.bookshopId}`, {
        method: 'DELETE',
      });

      if (!res.ok) {
        const errorData = await res.json();
        const alertMessage = `${errorData.name || 'Error'}: ${errorData.description || ''}`;
        throw new Error(alertMessage);
      }

      setShowModal(false);
      setShowSuccessModal(true);

    } catch (err: unknown) {
      if (err instanceof Error) {
        alert(err.message);
      } else {
        alert(String(err));
      }
      setShowModal(false);
    }
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
		<SuccessModal
		  show={showSuccessModal}
		  title="Bookshop Deleted"
		  message={`The bookshop "${bookshopToDelete?.name}" was successfully deleted.`}
		  isLoading={isSuccessModalLoading}
		  onConfirm={() => {
		    setIsSuccessModalLoading(true);
		    setTimeout(() => {
		      loadBookshops(paginatedResult.page, paginatedResult.size);
		      setShowSuccessModal(false);
		      setIsSuccessModalLoading(false);
		    }, 1000);
		  }}
		/>
    </div>
  );
};

export default BookshopListContainer;
