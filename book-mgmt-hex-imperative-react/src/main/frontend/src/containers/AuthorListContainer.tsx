import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthorList from '../components/AuthorList';
import ConfirmModal from '../components/ConfirmModal';
import type { Author } from '../types/author';
import type { PaginatedResult } from '../types/pagination';
import SuccessModal from '../components/SuccessModal';

const AuthorListContainer: React.FC = () => {
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();
  const [showModal, setShowModal] = useState(false);
  const [authorToDelete, setAuthorToDelete] = useState<Author | null>(null);
  const [showSuccessModal, setShowSuccessModal] = useState(false);
  const [isSuccessModalLoading, setIsSuccessModalLoading] = useState(false);


  const [paginatedResult, setPaginatedResult] = useState<PaginatedResult<Author>>({
    content: [],
    page: 0,
    size: 10,
    totalElements: 0,
    totalPages: 0,
    last: true,
  });

  const loadAuthors = async (page = 0, size = 10) => {
    try {
      const res = await fetch(`/authors?page=${page}&size=${size}`);
      if (!res.ok) {
        const errorData = await res.json();
        throw new Error(errorData.message || 'Error fetching authors');
      }
      const data: PaginatedResult<Author> = await res.json();
      setPaginatedResult(data);
      setError(null);
    } catch (err) {
      setError((err as Error).message);
    }
  };

  useEffect(() => {
    loadAuthors(0, paginatedResult.size);
	// eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleEdit = (id: number) => {
    navigate(`/edit-author/${id}`);
  };

  const handleDelete = (id: number) => {
    const author = paginatedResult.content.find((a) => a.authorId === id);
    setAuthorToDelete(author ?? null);
    setShowModal(true);
  };

  const handlePageChange = (newPage: number) => {
    loadAuthors(newPage, paginatedResult.size);
  };

  const confirmDelete = async () => {
    if (!authorToDelete) return;

    try {
      const res = await fetch(`/authors/${authorToDelete.authorId}`, {
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
      <AuthorList
        paginatedResult={paginatedResult}
        onEdit={handleEdit}
        onDelete={handleDelete}
        onPageChange={handlePageChange}
      />
      <ConfirmModal
        show={showModal}
        title="Confirm Deletion"
        message={`Are you sure you want to delete the author "${authorToDelete?.name} ${authorToDelete?.surname}"?`}
        onConfirm={confirmDelete}
        onCancel={() => setShowModal(false)}
      />
		<SuccessModal
		  show={showSuccessModal}
		  title="Author Deleted"
		  message={`The author "${authorToDelete?.name} ${authorToDelete?.surname}" was successfully deleted.`}
		  isLoading={isSuccessModalLoading}
		  onConfirm={() => {
		    setIsSuccessModalLoading(true);
		    setTimeout(() => {
		      loadAuthors(paginatedResult.page, paginatedResult.size);
		      setShowSuccessModal(false);
		      setIsSuccessModalLoading(false);
		    }, 1000);
		  }}
		/>
    </div>
  );
};

export default AuthorListContainer;
