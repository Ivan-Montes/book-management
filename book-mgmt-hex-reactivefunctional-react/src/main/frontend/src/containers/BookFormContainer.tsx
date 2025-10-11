import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import BookForm from '../components/BookForm';
import type { Book } from '../types/book';

const BookFormContainer: React.FC = () => {
  const [book, setBook] = useState<Book | null>(null);
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  useEffect(() => {
    if (id) {
      fetch(`/books/${id}`)
        .then((res) => {
          if (!res.ok) throw new Error('Error fetching book');
          return res.json();
        })
        .then((data: Book) => setBook(data))
        .catch((err) => {
          console.error(err);
          alert('Error loading book');
          navigate('/books');
        });
    }
  }, [id, navigate]);

  return <BookForm book={book} />;
};

export default BookFormContainer;
