import { useEffect, useState } from 'react';
import PublisherList from '../components/PublisherList';
import { useNavigate } from 'react-router-dom';
import ConfirmModal from '../components/ConfirmModal';

function PublisherListContainer() {
  const [error, setError] = useState(null);
  const navigate = useNavigate();
  const [showModal, setShowModal] = useState(false);
  const [publisherToDelete, setPublisherToDelete] = useState(null);

  const [paginatedResult, setPaginatedResult] = useState({
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
      const data = await res.json();
      setPaginatedResult(data);
      setError(null);
    } catch (err) {
      setError(err.message);
    }
  };

  useEffect(() => {
    loadPublishers(0, paginatedResult.size);
  }, []);

  const handleEdit = (id) => {
    console.log('Edit publisher with id:', id);
    navigate(`/edit-publisher/${id}`);
  };

  const handleDelete = (id) => {
    console.log('Delete publisher with id:', id);
    const publisher = paginatedResult.content.find(p => p.publisherId === id);
    setPublisherToDelete(publisher);
    setShowModal(true);
  };

  const handlePageChange = (newPage) => {
    loadPublishers(newPage, paginatedResult.size);
  };

  const confirmDelete = () => {
    fetch(`/publishers/${publisherToDelete.publisherId}`, { method: 'DELETE' })
      .then(res => {
        if (!res.ok) {
          return res.json().then(errorData => {
            console.error('Backend error:', errorData);
            const alertMessage = `${errorData.name || 'Error'}: ${errorData.description || ''}`;
            throw new Error(alertMessage);
          });
        }
        loadPublishers(paginatedResult.page, paginatedResult.size);
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
    </div>
  );
}

export default PublisherListContainer;
