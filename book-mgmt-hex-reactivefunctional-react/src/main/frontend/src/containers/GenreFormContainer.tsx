import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import GenreForm from '../components/GenreForm';
import type { Genre } from '../types/genre';

const GenreFormContainer: React.FC = () => {
  const [genre, setGenre] = useState<Genre | null>(null);
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  useEffect(() => {
    if (id) {
      fetch(`/genres/${id}`)
        .then((res) => {
          if (!res.ok) throw new Error('Error fetching genre');
          return res.json();
        })
        .then((data: Genre) => setGenre(data))
        .catch((err) => {
          console.error(err);
          alert('Error loading genre');
          navigate('/genres');
        });
    }
  }, [id, navigate]);

  return <GenreForm genre={genre} />;
};

export default GenreFormContainer;
