import { useEffect, useState } from 'react';
import GenreList from '../components/GenreList';
import { useNavigate } from 'react-router-dom';
import ConfirmModal from '../components/ConfirmModal';

function GenreListContainer() {
  const [error, setError] = useState(null);
  const navigate = useNavigate();
  const [showModal, setShowModal] = useState(false);
  const [genreToDelete, setGenreToDelete] = useState(null);

  const [paginatedResult, setPaginatedResult] = useState({
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
	    const data = await res.json();
	    setPaginatedResult(data);
	    setError(null);
	  } catch (err) {
	    setError(err.message);
	  }
	};
	
	useEffect(() => {
		loadGenres(0, paginatedResult.size);
	  }, []);

  const handleEdit = (id) => {
    console.log('Edit genre with id:', id);
	navigate(`/edit-genre/${id}`);
  };

  const handleDelete = (id) => {
    console.log('Delete genre with id:', id);
    const genre = paginatedResult.content.find(g => g.genreId === id);
    setGenreToDelete(genre);
    setShowModal(true);
  };

  const handlePageChange = (newPage) => {
	loadGenres(newPage, paginatedResult.size);
  };
  
  const confirmDelete = () => {
    fetch(`/genres/${genreToDelete.genreId}`, { method: 'DELETE' })
      .then(res => {
        if (!res.ok) {
			return res.json().then(errorData => {
				console.error('Backend error:', errorData);
				const alertMessage = `${errorData.name || 'Error'}: ${errorData.description || ''}`;
				throw new Error(alertMessage);
			   	});
		}
		loadGenres(paginatedResult.page, paginatedResult.size);
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
      </div>
    );
}

export default GenreListContainer;
