import { useEffect, useState } from 'react';
import PublisherList from '../components/PublisherList';
import { useNavigate } from 'react-router-dom';
import ConfirmModal from '../components/ConfirmModal';
import type { Publisher } from '../types/publisher';
import type { PaginatedResult } from '../types/pagination';
import SuccessModal from '../components/SuccessModal';

const PublisherListContainer: React.FC = () => {
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();
  const [showModal, setShowModal] = useState(false);
  const [publisherToDelete, setPublisherToDelete] = useState<Publisher | null>(null);
  const [showSuccessModal, setShowSuccessModal] = useState(false);
  const [isSuccessModalLoading, setIsSuccessModalLoading] = useState(false);

  const [paginatedResult, setPaginatedResult] = useState<PaginatedResult<Publisher>>({
    content: [],
    page: 0,
    size: 10,
    totalElements: 0,
    totalPages: 0,
    last: true,
  });

  const loadPublishers = async (page = 0, size = 10) => {
    try {
      const res = await fetch(`/publishers?page=${page}&size=${size}`);
      if (!res.ok) {
        const errorData = await res.json();
        throw new Error(errorData.message || 'Error fetching publishers');
      }
      const data: PaginatedResult<Publisher> = await res.json();
      setPaginatedResult(data);
      setError(null);
    } catch (err) {
      setError((err as Error).message);
    }
  };

  useEffect(() => {
    loadPublishers(0, paginatedResult.size);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleEdit = (id: string) => {
    navigate(`/edit-publisher/${id}`);
  };

  const handleDelete = (id: string) => {
    const publisher = paginatedResult.content.find((p) => p.publisherId === id);
    setPublisherToDelete(publisher ?? null);
    setShowModal(true);
  };

  const handlePageChange = (newPage: number) => {
    loadPublishers(newPage, paginatedResult.size);
  };

  const confirmDelete = async () => {
    if (!publisherToDelete) return;

    try {
      const res = await fetch(`/publishers/${publisherToDelete.publisherId}`, {
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
      <PublisherList
        paginatedResult={paginatedResult}
        onEdit={handleEdit}
        onDelete={handleDelete}
        onPageChange={handlePageChange}
      />
      <ConfirmModal
        show={showModal}
        title="Confirm Deletion"
        message={`Are you sure you want to delete the publisher "${publisherToDelete?.name}"?`}
        onConfirm={confirmDelete}
        onCancel={() => setShowModal(false)}
      />
	  <SuccessModal
	    show={showSuccessModal}
	    title="Publisher Deleted"
	    message={`The publisher "${publisherToDelete?.name}" was successfully deleted.`}
	    isLoading={isSuccessModalLoading}
	    onConfirm={() => {
	      setIsSuccessModalLoading(true);
	      setTimeout(() => {
	        loadPublishers(paginatedResult.page, paginatedResult.size);
	        setShowSuccessModal(false);
	        setIsSuccessModalLoading(false);
	      }, 1000);
	    }}
	  />
    </div>
  );
};

export default PublisherListContainer;
