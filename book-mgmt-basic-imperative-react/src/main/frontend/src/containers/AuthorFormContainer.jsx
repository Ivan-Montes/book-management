import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import AuthorForm from '../components/AuthorForm';

function AuthorFormContainer() {
  const [author, setAuthor] = useState(null);
  const { id } = useParams();
  const navigate = useNavigate();

  useEffect(() => {
    if (id) {
      fetch(`/authors/${id}`)
        .then(res => {
          if (!res.ok) throw new Error('Error fetching author');
          return res.json();
        })
        .then(data => setAuthor(data))
        .catch(err => {
          console.error(err);
          alert('Error loading author');
          navigate('/authors');
        });
    }
  }, [id, navigate]);

  return (
    <AuthorForm author={author} />
  );
}

export default AuthorFormContainer;
