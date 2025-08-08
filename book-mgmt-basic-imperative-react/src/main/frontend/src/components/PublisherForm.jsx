import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import PropTypes from 'prop-types';

function PublisherForm({ publisher }) {
  const [id, setId] = useState('');
  const [name, setName] = useState('');

  useEffect(() => {
    if (publisher) {
      setId(publisher?.publisherId || '');
      setName(publisher?.name || '');
    }
  }, [publisher]);

  const navigate = useNavigate();

  const title = id ? (
    <h2 className="text-center">Edit Publisher</h2>
  ) : (
    <h2 className="text-center">Create Publisher</h2>
  );

  const handleSubmit = async (e) => {
    e.preventDefault();

    const payload = { name };

    try {
      const response = await fetch(id ? `/publishers/${id}` : '/publishers', {
        method: id ? 'PUT' : 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload),
      });

      if (response.ok) {
        const result = await response.json();
        console.log('Publisher saved:', result);
        navigate('/publishers');
      } else {
        const errorData = await response.json();
        console.error('Save error:', errorData);
        alert(errorData.message || 'Error saving publisher');
      }
    } catch (error) {
      console.error('Network Error:', error);
      alert('Network error while saving publisher');
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

PublisherForm.propTypes = {
  publisher: PropTypes.shape({
    publisherId: PropTypes.number,
    name: PropTypes.string,
  }),
};

export default PublisherForm;
