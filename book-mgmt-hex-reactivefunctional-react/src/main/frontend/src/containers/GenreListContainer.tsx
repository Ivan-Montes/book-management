import { useEffect, useState } from 'react';
import GenreList from '../components/GenreList';
import { useNavigate } from 'react-router-dom';
import ConfirmModal from '../components/ConfirmModal';
import type { Genre } from '../types/genre';
import type { PaginatedResult } from '../types/pagination';
import SuccessModal from '../components/SuccessModal';

const GenreListContainer: React.FC = () => {
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();
  const [showModal, setShowModal] = useState(false);
  const [genreToDelete, setGenreToDelete] = useState<Genre | null>(null);
  const [showSuccessModal, setShowSuccessModal] = useState(false);
  const [isSuccessModalLoading, setIsSuccessModalLoading] = useState(false);


  const [paginatedResult, setPaginatedResult] = useState<PaginatedResult<Genre>>({
    content: [],
    page: 0,
    size: 10,
    totalElements: 0,
    totalPages: 0,
    last: true,
  });

  const loadGenres = async (page = 0, size = 10) => {
    try {
      const res = await fetch(`/genres?page=${page}&size=${size}`);
      if (!res.ok) {
        const errorData = await res.json();
        throw new Error(errorData.message || 'Error fetching genres');
      }
      const data: PaginatedResult<Genre> = await res.json();
      setPaginatedResult(data);
      setError(null);
    } catch (err) {
      setError((err as Error).message);
    }
  };

  useEffect(() => {
    loadGenres(0, paginatedResult.size);
	// eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleEdit = (id: string) => {
    navigate(`/edit-genre/${id}`);
  };

  const handleDelete = (id: string) => {
    const genre = paginatedResult.content.find((g) => g.genreId === id);
    setGenreToDelete(genre ?? null);
    setShowModal(true);
  };

  const handlePageChange = (newPage: number) => {
    loadGenres(newPage, paginatedResult.size);
  };

  const confirmDelete = async () => {
    if (!genreToDelete) return;

    try {
      const res = await fetch(`/genres/${genreToDelete.genreId}`, {
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
      <GenreList
        paginatedResult={paginatedResult}
        onEdit={handleEdit}
        onDelete={handleDelete}
        onPageChange={handlePageChange}
      />
      <ConfirmModal
        show={showModal}
        title="Confirm Deletion"
        message={`Are you sure you want to delete the genre "${genreToDelete?.name}"?`}
        onConfirm={confirmDelete}
        onCancel={() => setShowModal(false)}
      />
		<SuccessModal
		  show={showSuccessModal}
		  title="Genre Deleted"
		  message={`The genre "${genreToDelete?.name}" was successfully deleted.`}
		  isLoading={isSuccessModalLoading}
		  onConfirm={() => {
		    setIsSuccessModalLoading(true);
		    setTimeout(() => {
		      loadGenres(paginatedResult.page, paginatedResult.size);
		      setShowSuccessModal(false);
		      setIsSuccessModalLoading(false);
		    }, 1000);
		  }}
		/>
    </div>
  );
};

export default GenreListContainer;
