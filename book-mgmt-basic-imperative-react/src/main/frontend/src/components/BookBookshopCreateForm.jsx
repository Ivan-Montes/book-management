import { useState } from 'react';
import PropTypes from 'prop-types';

function BookBookshopCreateForm({ booksPaginated, bookshopsPaginated, onSubmit }) {
  const books = Array.isArray(booksPaginated?.content) ? booksPaginated.content : [];
  const bookshops = Array.isArray(bookshopsPaginated?.content) ? bookshopsPaginated.content : [];

  const [bookId, setBookId] = useState('');
  const [bookshopId, setBookshopId] = useState('');
  const [price, setPrice] = useState('');
  const [units, setUnits] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!bookId || !bookshopId || price === '' || units === '') {
      alert('All fields are required');
      return;
    }
    onSubmit({ bookId: Number(bookId), bookshopId: Number(bookshopId), price: Number(price), units: Number(units) });
  };

  return (
    <form onSubmit={handleSubmit}>
      <div className="row">
        <div className="col-md-6 mb-3">
          <label htmlFor="bookSelect" className="form-label">Select Book</label>
          <select
            className="form-select"
            id="bookSelect"
            value={bookId}
            onChange={(e) => setBookId(e.target.value)}
            required
          >
            <option value="">-- Select a Book --</option>
            {books.map((book) => (
              <option key={book.bookId} value={book.bookId}>
                {book.title} ({book.isbn})
              </option>
            ))}
          </select>
        </div>

        <div className="col-md-6 mb-3">
          <label htmlFor="bookshopSelect" className="form-label">Select Bookshop</label>
          <select
            className="form-select"
            id="bookshopSelect"
            value={bookshopId}
            onChange={(e) => setBookshopId(e.target.value)}
            required
          >
            <option value="">-- Select a Bookshop --</option>
            {bookshops.map((bs) => (
              <option key={bs.bookshopId} value={bs.bookshopId}>
                {bs.name}
              </option>
            ))}
          </select>
        </div>
      </div>

      <div className="row">
        <div className="col-md-6 mb-3">
          <label htmlFor="inputPrice" className="form-label">Price</label>
          <input
            type="number"
            className="form-control"
            id="inputPrice"
            min="0"
            max="1000000"
            step="0.01"
            value={price}
            onChange={(e) => setPrice(e.target.value)}
            required
          />
        </div>

        <div className="col-md-6 mb-3">
          <label htmlFor="inputUnits" className="form-label">Units</label>
          <input
            type="number"
            className="form-control"
            id="inputUnits"
            min="0"
            max="99"
            value={units}
            onChange={(e) => setUnits(e.target.value)}
            required
          />
        </div>
      </div>

      <div className="text-center mt-3">
        <button type="submit" className="btn btn-success">Save</button>
      </div>
    </form>
  );
}

BookBookshopCreateForm.propTypes = {
  booksPaginated: PropTypes.shape({
    content: PropTypes.arrayOf(
      PropTypes.shape({
        bookId: PropTypes.number.isRequired,
        title: PropTypes.string.isRequired,
        isbn: PropTypes.string.isRequired,
      })
    ).isRequired,
  }).isRequired,

  bookshopsPaginated: PropTypes.shape({
    content: PropTypes.arrayOf(
      PropTypes.shape({
        bookshopId: PropTypes.number.isRequired,
        name: PropTypes.string.isRequired,
      })
    ).isRequired,
  }).isRequired,

  onSubmit: PropTypes.func.isRequired,
};

export default BookBookshopCreateForm;
