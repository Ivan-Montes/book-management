import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import PropTypes from 'prop-types';

function BookshopForm({ bookshop }) {
  const [id, setId] = useState('');
  const [name, setName] = useState('');

  const navigate = useNavigate();

  useEffect(() => {
    if (bookshop) {
      setId(bookshop?.bookshopId || '');
      setName(bookshop?.name || '');
    }
  }, [bookshop]);

  const title = id ? (
    <h2 className="text-center">Edit Bookshop</h2>
  ) : (
    <h2 className="text-center">Create Bookshop</h2>
  );

  const handleSubmit = async (e) => {
    e.preventDefault();

    const payload = { name };

    try {
      const response = await fetch(id ? `/bookshops/${id}` : '/bookshops', {
        method: id ? 'PUT' : 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload),
      });

      if (response.ok) {
        const result = await response.json();
        console.log('Bookshop saved:', result);
        navigate('/bookshops');
      } else {
        const errorData = await response.json();
        console.error('Save error:', errorData);
        alert(errorData.message || 'Error saving bookshop');
      }
    } catch (error) {
      console.error('Network Error:', error);
      alert('Network error while saving bookshop');
    }
  };

  return (
    <div className="container">
      {title}
      <form method="post" onSubmit={handleSubmit}>
        <div className="form-group mb-3">
          <label htmlFor="inputName">Name</label>
          <input
            type="text"
            className="form-control form-control-lg"
            id="inputName"
            placeholder="Enter name"
            name="name"
            pattern="^[a-zA-ZñÑáéíóúÁÉÍÓÚ]+[a-zA-ZñÑáéíóúÁÉÍÓÚ\s\-\.,&:]*"
            minLength="1"
            maxLength="50"
            required
            value={name}
            onChange={(e) => setName(e.target.value)}
          />
        </div>

        <button type="submit" className="btn btn-primary">Save</button>
      </form>
    </div>
  );
}

BookshopForm.propTypes = {
  bookshop: PropTypes.shape({
    bookshopId: PropTypes.number,
    name: PropTypes.string,
  }),
};

export default BookshopForm;
