import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import BookList from '../components/BookList';
import ConfirmModal from '../components/ConfirmModal';
import type { Book } from '../types/book';
import type { PaginatedResult } from '../types/pagination';
import SuccessModal from '../components/SuccessModal';

const BookListContainer: React.FC = () => {
  const [error, setError] = useState<string | null>(null);
  const [showModal, setShowModal] = useState(false);
  const [bookToDelete, setBookToDelete] = useState<Book | null>(null);
  const [showSuccessModal, setShowSuccessModal] = useState(false);
  const [isSuccessModalLoading, setIsSuccessModalLoading] = useState(false);

  const [paginatedResult, setPaginatedResult] = useState<PaginatedResult<Book>>({
    content: [],
    page: 0,
    size: 10,
    totalElements: 0,
    totalPages: 0,
    last: true,
  });

  const navigate = useNavigate();

  const loadBooks = async (page = 0, size = 10) => {
    try {
      const res = await fetch(`/books?page=${page}&size=${size}`);
      if (!res.ok) {
        const errorData = await res.json();
        throw new Error(errorData.message || 'Error fetching books');
      }
      const data: PaginatedResult<Book> = await res.json();

      setPaginatedResult(data);
      setError(null);
    } catch (err) {
      setError((err as Error).message);
    }
  };

  useEffect(() => {
    loadBooks(0, paginatedResult.size);
	// eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleEdit = (id: number) => {
    navigate(`/edit-book/${id}`);
  };

  const handleDelete = (id: number) => {
    const book = paginatedResult.content.find((b) => b.bookId === id) ?? null;
    setBookToDelete(book);
    setShowModal(true);
  };

  const handlePageChange = (newPage: number) => {
    loadBooks(newPage, paginatedResult.size);
  };

  const confirmDelete = async () => {
    if (!bookToDelete) return;

    try {
      const res = await fetch(`/books/${bookToDelete.bookId}`, {
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
      <BookList
        paginatedResult={paginatedResult}
        onEdit={handleEdit}
        onDelete={handleDelete}
        onPageChange={handlePageChange}
      />
      <ConfirmModal
        show={showModal}
        title="Confirm Deletion"
        message={`Are you sure you want to delete the book "${bookToDelete?.title}"?`}
        onConfirm={confirmDelete}
        onCancel={() => setShowModal(false)}
      />
		<SuccessModal
		  show={showSuccessModal}
		  title="Book Deleted"
		  message={`The book "${bookToDelete?.title}" was successfully deleted.`}
		  isLoading={isSuccessModalLoading}
		  onConfirm={() => {
		    setIsSuccessModalLoading(true);
		    setTimeout(() => {
		      loadBooks(paginatedResult.page, paginatedResult.size);
		      setShowSuccessModal(false);
		      setIsSuccessModalLoading(false);
		    }, 1000);
		  }}
		/>
    </div>
  );
};

export default BookListContainer;
