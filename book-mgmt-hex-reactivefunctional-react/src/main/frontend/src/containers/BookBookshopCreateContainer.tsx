import { useEffect, useState } from 'react';
import BookBookshopCreateForm from '../components/BookBookshopCreateForm';
import type { Book } from '../types/book';
import type { Bookshop } from '../types/bookshop';
import type { PaginatedResult } from '../types/pagination';

const BookBookshopCreateContainer: React.FC = () => {
  const [books, setBooks] = 	useState<PaginatedResult<Book>>({
	    content: [],
	    page: 0,
	    size: 10,
	    totalElements: 0,
	    totalPages: 0,
	    last: true,
	  });
  const [bookshops, setBookshops] =   useState<PaginatedResult<Bookshop>>({
      content: [],
      page: 0,
      size: 10,
      totalElements: 0,
      totalPages: 0,
      last: true,
    });
  const [error, setError] = useState<string | null>(null);

  const fetchBooks = async () => {
    try {
      const res = await fetch('/books');
      if (!res.ok) throw new Error('Failed to load books');
      const data: PaginatedResult<Book> = await res.json();
      setBooks(data);
    } catch (err) {
		setError((err as Error).message);
    }
  };

  const fetchBookshops = async () => {
    try {
      const res = await fetch('/bookshops');
      if (!res.ok) throw new Error('Failed to load bookshops');
      const data: PaginatedResult<Bookshop> = await res.json();
      setBookshops(data);
    } catch (err) {
		setError((err as Error).message);
    }
  };

  useEffect(() => {
    fetchBooks();
    fetchBookshops();
  }, []);

  return (
  <div>
      {error && <div className="alert alert-danger">{error}</div>}
      <BookBookshopCreateForm
        booksPaginated={books}
        bookshopsPaginated={bookshops}
      />
    </div>
  );
};

export default BookBookshopCreateContainer;
