import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import BookshopForm from '../components/BookshopForm';
import type { Bookshop } from '../types/bookshop';

const BookshopFormContainer: React.FC = () => {
  const [bookshop, setBookshop] = useState<Bookshop | null>(null);
  const { id } = useParams<{ id?: string }>();
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
        .then((data: Bookshop) => setBookshop(data))
        .catch(err => {
          console.error(err);
          alert('Error loading bookshop');
          navigate('/bookshops');
        });
    }
  }, [id, navigate]);

  return <BookshopForm bookshop={bookshop} />;
};

export default BookshopFormContainer;
