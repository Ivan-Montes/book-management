import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import BookBookshopForm from '../components/BookBookshopForm';
import type { BookBookshop } from '../types/bookBookshop';

const BookBookshopFormContainer: React.FC = () => {
  const [bookBookshop, setBookBookshop] = useState<BookBookshop | null>(null);
  const { bookId, bookshopId } = useParams<{ bookId: string; bookshopId: string }>();
  const navigate = useNavigate();

  useEffect(() => {
    if (bookId && bookshopId) {
      fetch(`/bookbookshops/book/${bookId}/bookshop/${bookshopId}`)
        .then((res) => {
          if (!res.ok) throw new Error('Error fetching book-bookshop relation');
          return res.json();
        })
        .then((data: BookBookshop) => setBookBookshop(data))
        .catch((err) => {
          console.error('Error fetching book-bookshop data:', err);
          alert('Error loading book-bookshop relation');
          navigate('/bookbookshops');
        });
    }
  }, [bookId, bookshopId, navigate]);

  return <BookBookshopForm bookBookshop={bookBookshop} />;
};

export default BookBookshopFormContainer;
