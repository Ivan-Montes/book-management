import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import BookBookshopForm from '../components/BookBookshopForm';

function BookBookshopFormContainer() {
  const [bookBookshop, setBookBookshop] = useState(null);
  const { bookId, bookshopId } = useParams();
  const navigate = useNavigate();

  useEffect(() => {
    if (bookId && bookshopId) {
      fetch(`/bookbookshops/book/${bookId}/bookshop/${bookshopId}`)
        .then(res => {
          if (!res.ok) throw new Error('Error fetching book-bookshop relation');
          return res.json();
        })
        .then(data => setBookBookshop(data))
        .catch(err => {
          console.error('Error fetching book-bookshop data:', err);
          alert('Error loading book-bookshop relation');
          navigate('/bookbookshops');
        });
    }
  }, [bookId, bookshopId, navigate]);

  return (
    <BookBookshopForm bookBookshop={bookBookshop} />
  );
}

export default BookBookshopFormContainer;
