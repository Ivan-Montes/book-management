import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import BookBookshopCreateForm from '../components/BookBookshopCreateForm';

function BookBookshopCreateContainer() {
  const [books, setBooks] = useState([]);
  const [bookshops, setBookshops] = useState([]);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const fetchBooks = async () => {
    try {
      const res = await fetch('/books');
      if (!res.ok) throw new Error('Failed to load books');
      const data = await res.json();
      setBooks(data);
    } catch (err) {
      setError(err.message);
    }
  };

  const fetchBookshops = async () => {
    try {
      const res = await fetch('/bookshops');
      if (!res.ok) throw new Error('Failed to load bookshops');
      const data = await res.json();
      setBookshops(data);
    } catch (err) {
      setError(err.message);
    }
  };

  useEffect(() => {
    fetchBooks();
    fetchBookshops();
  }, []);

  const handleSubmit = async (formData) => {
    const { bookId, bookshopId, price, units } = formData;

    try {
      const response = await fetch('/bookbookshops', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ bookId, bookshopId, price, units }),
      });

      if (response.ok) {
        navigate('/bookbookshops');
      } else {
        const errData = await response.json();
        throw new Error(errData.message || 'Error creating record');
      }
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div className="container mt-3">
      <h2 className="text-center">Add Book to Bookshop</h2>
      {error && <div className="alert alert-danger">{error}</div>}
      <BookBookshopCreateForm
        booksPaginated={books}
        bookshopsPaginated={bookshops}
        onSubmit={handleSubmit}
      />
    </div>
  );
}

export default BookBookshopCreateContainer;
