import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import GenreForm from '../components/GenreForm';

function GenreFormContainer() {
  const [genre, setGenre] = useState(null);
  const { id } = useParams();
  const navigate = useNavigate();

  useEffect(() => {
    if (id) {
      fetch(`/genres/${id}`)
        .then(res => {
          if (!res.ok) throw new Error('Error fetching genre');
          return res.json();
        })
        .then(data => setGenre(data))
        .catch(err => {
          console.error(err);
          alert('Error loading genre');
          navigate('/genres');
        });
    }
  }, [id, navigate]);

  return (
    <GenreForm genre={genre} />
  );
}

export default GenreFormContainer;
