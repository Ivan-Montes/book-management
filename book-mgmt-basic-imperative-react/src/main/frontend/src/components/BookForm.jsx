import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import PropTypes from 'prop-types';
import AuthorModal from './AuthorModal';
import GenreModal from './GenreModal';
import PublisherModal from './PublisherModal';

function BookForm({ book }) {
  const [isbn, setIsbn] = useState('');
  const [title, setTitle] = useState('');
  const [publisher, setPublisher] = useState(null);
  const [genre, setGenre] = useState(null);
  const [authors, setAuthors] = useState([]);
  const [showPublisherModal, setShowPublisherModal] = useState(false);
  const [showGenreModal, setShowGenreModal] = useState(false);
  const [showAuthorModal, setShowAuthorModal] = useState(false);

  const navigate = useNavigate();

  useEffect(() => {
    if (book) {
      setIsbn(book.isbn || '');
      setTitle(book.title || '');
      setPublisher(book.publisherId ? { publisherId: book.publisherId, name: book.publisherName } : null);
      setGenre(book.genreId ? { genreId: book.genreId, name: book.genreName } : null);
      setAuthors(book.authorsSet || []);
    }
  }, [book]);

  const titleText = book ? <h2 className="text-center">Edit Book</h2> : <h2 className="text-center">Create Book</h2>;

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!publisher || !genre || authors.length === 0) {
      alert('Please select publisher, genre and at least one author');
      return;
    }

    const payload = {
      isbn,
      title,
      publisherId: publisher.publisherId,
      genreId: genre.genreId,
      authorId: authors.map(a => a.authorId),
	  authorsSet: authors,
    };

    try {
      const response = await fetch(book ? `/books/${book.bookId}` : '/books', {
        method: book ? 'PUT' : 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      });

      if (response.ok) {
        const result = await response.json();
        console.log('Book saved:', result);
        navigate('/books');
      } else {
        const errorData = await response.json();
        console.error('Error in save process:', errorData);
        alert('Error saving book: ' + (errorData.message || 'Unknown error'));
      }
    } catch (error) {
      console.error('Network Error:', error);
      alert('Network error: ' + error.message);
    }
  };

  return (
    <div className="container">
      {titleText}
      <form onSubmit={handleSubmit}>
        <div className="form-group mb-3">
          <label htmlFor="inputIsbn" className="form-label">ISBN</label>
          <input
            type="text"
            className="form-control"
            id="inputIsbn"
            placeholder="Enter ISBN"
            name="isbn"
            pattern="[\d]{10,13}"
            minLength="10"
            maxLength="13"
            required
            value={isbn}
            onChange={(e) => setIsbn(e.target.value)}
          />
        </div>

        <div className="form-group mb-3">
          <label htmlFor="inputTitle" className="form-label">Title</label>
          <input
            type="text"
            className="form-control"
            id="inputTitle"
            placeholder="Enter Title"
            name="title"
            pattern="^[a-zA-ZñÑáéíóúÁÉÍÓÚ¿¡]+[a-zA-Z0-9ñÑáéíóúÁÉÍÓÚ\s\-\?¿\!¡\.&,:]*"
            minLength="1"
            maxLength="100"
            required
            value={title}
            onChange={(e) => setTitle(e.target.value)}
          />
        </div>

        <div className="form-group mb-3">
          <label className="form-label">Publisher</label>
          <button
            type="button"
            className="form-control text-start"
            onClick={() => setShowPublisherModal(true)}
          >
            {publisher?.name || 'Select publisher'}
          </button>
        </div>

        <div className="form-group mb-3">
          <label className="form-label">Genre</label>
          <button
            type="button"
            className="form-control text-start"
            onClick={() => setShowGenreModal(true)}
          >
            {genre?.name || 'Select genre'}
          </button>
        </div>

        <div className="form-group mb-3">
          <label className="form-label">Authors</label>
          <button
            type="button"
            className="form-control text-start"
            onClick={() => setShowAuthorModal(true)}
          >
            {authors.length > 0
              ? authors.map(a => `${a.name} ${a.surname}`).join(', ')
              : 'Select authors'}
          </button>
          <div className="mt-2">
            {authors.map(author => (
              <span key={author.authorId} className="badge bg-secondary me-1">
                {author.name} {author.surname}
              </span>
            ))}
          </div>
        </div>

        <button type="submit" className="btn btn-primary">Save</button>
      </form>

      {showPublisherModal && (
        <PublisherModal
          selectedPublisher={publisher}
          onSelect={(pub) => {
            setPublisher(pub);
            setShowPublisherModal(false);
          }}
          onClose={() => setShowPublisherModal(false)}
        />
      )}
      {showGenreModal && (
        <GenreModal
          selectedGenre={genre}
          onSelect={(gen) => {
            setGenre(gen);
            setShowGenreModal(false);
          }}
          onClose={() => setShowGenreModal(false)}
        />
      )}
      {showAuthorModal && (
        <AuthorModal
          selectedAuthors={authors}
          onSelect={(auths) => {
            setAuthors(auths);
            setShowAuthorModal(false);
          }}
          onClose={() => setShowAuthorModal(false)}
        />
      )}
    </div>
  );
}

BookForm.propTypes = {
  book: PropTypes.shape({
    bookId: PropTypes.number,
    isbn: PropTypes.string,
    title: PropTypes.string,
    publisherId: PropTypes.number,
    publisherName: PropTypes.string,
    genreId: PropTypes.number,
    genreName: PropTypes.string,
    authorId: PropTypes.arrayOf(PropTypes.number),
    authorsSet: PropTypes.arrayOf(
      PropTypes.shape({
        authorId: PropTypes.number,
        name: PropTypes.string,
        surname: PropTypes.string,
      })
    ),
  }),
};

export default BookForm;
