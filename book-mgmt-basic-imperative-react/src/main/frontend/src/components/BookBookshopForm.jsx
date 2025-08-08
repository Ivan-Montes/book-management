import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import PropTypes from 'prop-types';

function BookBookshopForm({ bookBookshop }) {
  const [bookId, setBookId] = useState('');
  const [isbn, setIsbn] = useState('');
  const [title, setTitle] = useState('');
  const [bookshopId, setBookshopId] = useState('');
  const [name, setName] = useState('');
  const [price, setPrice] = useState('');
  const [units, setUnits] = useState('');

  useEffect(() => {
    if (bookBookshop) {
      setBookId(bookBookshop.bookId || '');
      setIsbn(bookBookshop.isbn || '');
      setTitle(bookBookshop.title || '');
      setBookshopId(bookBookshop.bookshopId || '');
      setName(bookBookshop.name || '');
      setPrice(bookBookshop.price?.toString() || '');
      setUnits(bookBookshop.units?.toString() || '');
    }
  }, [bookBookshop]);

  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();

    const payload = {
      bookId,
      isbn,
      title,
      bookshopId,
      name,
      price: parseFloat(price),
      units: parseInt(units, 10),
    };

    try {
      const response = await fetch(`/bookbookshops/book/${bookId}/bookshop/${bookshopId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload),
      });

      if (response.ok) {
        const result = await response.json();
        console.log('BookBookshop saved:', result);
        navigate('/bookbookshops');
      } else {
        const errorData = await response.json();
        console.error('Error updating:', errorData);
      }
    } catch (error) {
      console.error('Network error:', error);
    }
  };

  return (
    <div className="container">
      <h2 className="text-center">Edit Book in Bookshop</h2>
      <form onSubmit={handleSubmit}>
        <div className="row mb-3">
          <div className="col-md-4">
            <label htmlFor="inputBookId" className="form-label">Book ID</label>
            <input
              type="text"
              className="form-control form-control-lg bg-secondary bg-gradient text-white"
              id="inputBookId"
              value={bookId}
              disabled
            />
          </div>
          <div className="col-md-4">
            <label htmlFor="inputIsbn" className="form-label">ISBN</label>
            <input
              type="text"
              className="form-control form-control-lg bg-secondary bg-gradient text-white"
              id="inputIsbn"
              value={isbn}
              disabled
            />
          </div>
          <div className="col-md-4">
            <label htmlFor="inputTitle" className="form-label">Title</label>
            <input
              type="text"
              className="form-control form-control-lg bg-secondary bg-gradient text-white"
              id="inputTitle"
              value={title}
              disabled
            />
          </div>
        </div>

        <div className="row mb-3">
          <div className="col-md-6">
            <label htmlFor="inputPrice" className="form-label">Price</label>
            <input
              type="number"
              className="form-control form-control-lg"
              id="inputPrice"
              placeholder="Enter price"
              min="0"
              max="999"
              step="0.01"
              required
              value={price}
              onChange={(e) => setPrice(e.target.value)}
            />
          </div>
          <div className="col-md-6">
            <label htmlFor="inputUnits" className="form-label">Units</label>
            <input
              type="number"
              className="form-control form-control-lg"
              id="inputUnits"
              placeholder="Enter units"
              min="0"
              max="99"
              required
              value={units}
              onChange={(e) => setUnits(e.target.value)}
            />
          </div>
        </div>

        <div className="row mb-3">
          <div className="col-md-6">
            <label htmlFor="inputBookshopId" className="form-label">Bookshop ID</label>
            <input
              type="text"
              className="form-control form-control-lg bg-secondary bg-gradient text-white"
              id="inputBookshopId"
              value={bookshopId}
              disabled
            />
          </div>
          <div className="col-md-6">
            <label htmlFor="inputBookshopName" className="form-label">Bookshop Name</label>
            <input
              type="text"
              className="form-control form-control-lg bg-secondary bg-gradient text-white"
              id="inputBookshopName"
              value={name}
              disabled
            />
          </div>
        </div>

        <div className="mt-3">
          <button type="submit" className="btn btn-outline-info">Save</button>
        </div>
      </form>
    </div>
  );
}

BookBookshopForm.propTypes = {
  bookBookshop: PropTypes.shape({
    bookId: PropTypes.number,
    isbn: PropTypes.string,
    title: PropTypes.string,
    bookshopId: PropTypes.number,
    name: PropTypes.string,
    price: PropTypes.number,
    units: PropTypes.number,
  }),
};

export default BookBookshopForm;
