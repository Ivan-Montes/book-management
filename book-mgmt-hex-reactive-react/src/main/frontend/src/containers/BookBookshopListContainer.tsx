import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import ConfirmModal from '../components/ConfirmModal';
import BookBookshopList from '../components/BookBookshopList';
import type { BookBookshop } from '../types/bookBookshop';
import type { PaginatedResult } from '../types/pagination';
import SuccessModal from '../components/SuccessModal';

const BookBookshopListContainer: React.FC = () => {
  const [error, setError] = useState<string | null>(null);
  const [showModal, setShowModal] = useState(false);
  const [itemToDelete, setItemToDelete] = useState<BookBookshop | null>(null);
  const navigate = useNavigate();
  const [showSuccessModal, setShowSuccessModal] = useState(false);
  const [isSuccessModalLoading, setIsSuccessModalLoading] = useState(false);


  const [paginatedResult, setPaginatedResult] = useState<PaginatedResult<BookBookshop>>({
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
      const data: PaginatedResult<BookBookshop> = await res.json();
      setPaginatedResult(data);
      setError(null);
    } catch (err) {
      setError((err as Error).message);
    }
  };

  useEffect(() => {
    loadBookBookshops(0, paginatedResult.size);
	// eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleEdit = (bookId: string, bookshopId: string) => {
    navigate(`/edit-bookbookshop/book/${bookId}/bookshop/${bookshopId}`);
  };

  const handleDelete = (bookId: string, bookshopId: string) => {
    const item = paginatedResult.content.find(
      (i) => i.bookId === bookId && i.bookshopId === bookshopId
    );
    setItemToDelete(item ?? null);
    setShowModal(true);
  };

  const handlePageChange = (newPage: number) => {
    loadBookBookshops(newPage, paginatedResult.size);
  };

  const confirmDelete = async () => {
    if (!itemToDelete) return;
	
	const { bookId, bookshopId } = itemToDelete;

    try {
      const res = await fetch(`/bookbookshops/book/${bookId}/bookshop/${bookshopId}`, {
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
		<SuccessModal
		  show={showSuccessModal}
		  title="Publisher Deleted"
		  message={`The book "${itemToDelete?.title}" from bookshop "${itemToDelete?.name}" was successfully deleted.`}
		  isLoading={isSuccessModalLoading}
		  onConfirm={() => {
		    setIsSuccessModalLoading(true);
		    setTimeout(() => {
		      loadBookBookshops(paginatedResult.page, paginatedResult.size);
		      setShowSuccessModal(false);
		      setIsSuccessModalLoading(false);
		    }, 1000);
		  }}
		/>
    </div>
  );
};

export default BookBookshopListContainer;
