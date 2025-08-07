import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import BookshopForm from '../components/BookshopForm';

function BookshopFormContainer() {
  const [bookshop, setBookshop] = useState(null);
  const { id } = useParams();
  const navigate = useNavigate();

  useEffect(() => {
    if (id) {
      fetch(`/bookshops/${id}`)
        .then(res => {
          if (!res.ok) {
            throw new Error('Error fetching bookshop');
          }
          return res.json();
        })
        .then(data => setBookshop(data))
        .catch(err => {
          console.error(err);
          alert('Error loading bookshop');
          navigate('/bookshops');
        });
    }
  }, [id, navigate]);

  return (
    <BookshopForm bookshop={bookshop} />
  );
}

export default BookshopFormContainer;
