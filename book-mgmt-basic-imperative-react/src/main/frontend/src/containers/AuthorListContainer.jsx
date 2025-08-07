import { useEffect, useState } from 'react';
import AuthorList from '../components/AuthorList';
import { useNavigate } from 'react-router-dom';
import ConfirmModal from '../components/ConfirmModal';

function AuthorListContainer() {
  const [error, setError] = useState(null);
  const navigate = useNavigate();
  const [showModal, setShowModal] = useState(false);
  const [authorToDelete, setAuthorToDelete] = useState(null);

  const [paginatedResult, setPaginatedResult] = useState({
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
      const data = await res.json();
      setPaginatedResult(data);
      setError(null);
    } catch (err) {
      setError(err.message);
    }
  };

  useEffect(() => {
    loadAuthors(0, paginatedResult.size);
  }, []);

  const handleEdit = (id) => {
    navigate(`/edit-author/${id}`);
  };

  const handleDelete = (id) => {
    const author = paginatedResult.content.find(a => a.authorId === id);
    setAuthorToDelete(author);
    setShowModal(true);
  };

  const handlePageChange = (newPage) => {
    loadAuthors(newPage, paginatedResult.size);
  };

  const confirmDelete = () => {
    fetch(`/authors/${authorToDelete.authorId}`, { method: 'DELETE' })
      .then(res => {
        if (!res.ok) {
          return res.json().then(errorData => {
            const alertMessage = `${errorData.name || 'Error'}: ${errorData.description || ''}`;
            throw new Error(alertMessage);
          });
        }
        loadAuthors(paginatedResult.page, paginatedResult.size);
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
    </div>
  );
}

export default AuthorListContainer;
