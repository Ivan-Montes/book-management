import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import PropTypes from 'prop-types';

function AuthorForm({ author }) {
  const [id, setId] = useState('');
  const [name, setName] = useState('');
  const [surname, setSurname] = useState('');

  useEffect(() => {
    if (author) {
      setId(author?.authorId || '');
      setName(author?.name || '');
      setSurname(author?.surname || '');
    }
  }, [author]);

  const navigate = useNavigate();

  const title = id ? (
    <h2 className="text-center">Edit Author</h2>
  ) : (
    <h2 className="text-center">Create Author</h2>
  );

  const handleSubmit = async (e) => {
    e.preventDefault();

    const payload = { name, surname };

    try {
      const response = await fetch(id ? `/authors/${id}` : '/authors', {
        method: id ? 'PUT' : 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload),
      });

      if (response.ok) {
        const result = await response.json();
        console.log('Author saved:', result);
        navigate('/authors');
      } else {
        const err = await response.json();
        throw new Error(err.message || 'Error in save process');
      }
    } catch (error) {
      console.error('Network Error:', error);
    }
  };

  return (
    <div className="container">
      {title}
      <form onSubmit={handleSubmit}>
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

        <div className="form-group mb-3">
          <label htmlFor="inputSurname">Surname</label>
          <input
            type="text"
            className="form-control form-control-lg"
            id="inputSurname"
            placeholder="Enter surname"
            name="surname"
            pattern="^[a-zA-ZñÑáéíóúÁÉÍÓÚ]+[a-zA-ZñÑáéíóúÁÉÍÓÚ\s\-\.,&:]*"
            minLength="1"
            maxLength="50"
            required
            value={surname}
            onChange={(e) => setSurname(e.target.value)}
          />
        </div>

        <button type="submit" className="btn btn-primary">Save</button>
      </form>
    </div>
  );
}

AuthorForm.propTypes = {
  author: PropTypes.shape({
    authorId: PropTypes.number,
    name: PropTypes.string,
    surname: PropTypes.string,
  }),
};

export default AuthorForm;
