import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import AuthorForm from '../components/AuthorForm';
import type { Author } from '../types/author';

const AuthorFormContainer: React.FC = () => {
  const [author, setAuthor] = useState<Author | null>(null);
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  useEffect(() => {
    if (id) {
      fetch(`/authors/${id}`)
        .then(res => {
          if (!res.ok) throw new Error('Error fetching author');
          return res.json();
        })
        .then((data: Author) => setAuthor(data))
        .catch(err => {
          console.error(err);
          alert('Error loading author');
          navigate('/authors');
        });
    }
  }, [id, navigate]);

  return <AuthorForm author={author} />;
};

export default AuthorFormContainer;
