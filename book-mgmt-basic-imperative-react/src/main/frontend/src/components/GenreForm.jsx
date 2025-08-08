import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import PropTypes from 'prop-types';

function GenreForm({genre}) {

	const [id, setId] = useState('');
	const [name, setName] = useState('');
	const [description, setDescription] = useState('');
	
	useEffect(() => {
	  if (genre) {
	    setId(genre?.genreId || '');
	    setName(genre?.name || '');
	    setDescription(genre?.description || '');
	  }
	}, [genre]);
	
	const navigate = useNavigate();
	
	const title = id ? (
	  <h2 className="text-center">Edit Genres</h2>
	) : (
	  <h2 className="text-center">Create Genres</h2>
	);
	
	const handleSubmit = async (e) => {
	  e.preventDefault();

	  const payload = {
	    name,
	    description,
	  };

	  try {
	    const response = await fetch(id ? `/genres/${id}` : '/genres', {
	      method: id ? 'PUT' : 'POST',
	      headers: {
	        'Content-Type': 'application/json',
	      },
	      body: JSON.stringify(payload),
	    });

	    if (response.ok) {
	      const result = await response.json();
	      console.log('Genre saved:', result);
	      navigate('/genres');
	    } else {
	      console.error('Error in save process');
	    }
	  } catch (error) {
	    console.error('Network Error:', error);
	  }
	};

	return (
		<div className="container">
		{title}
			<form method="post" action="/addGenre" onSubmit={handleSubmit}>
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
					<label htmlFor="inputDescription">Description</label>
					<input
						type="text"
						className="form-control form-control-lg"
						id="inputDescription"
						aria-describedby="descHelp"
						placeholder="Enter a description"
						name="description"
						pattern="^[a-zA-ZñÑáéíóúÁÉÍÓÚ¿¡]+[a-zA-Z0-9ñÑáéíóúÁÉÍÓÚ\s\-\?¿\!¡\.&,:]*"
						minLength="1"
						maxLength="100"
						required
						value={description}
						onChange={(e) => setDescription(e.target.value)}
					/>
					<small id="descHelp" className="form-text text-muted">
						A brief description for the literary genre
					</small>
				</div>

				<button type="submit" className="btn btn-primary">Save</button>
			</form>
		</div>
	);

}

GenreForm.propTypes = {
  genre: PropTypes.shape({
    genreId: PropTypes.number,
    name: PropTypes.string,
    description: PropTypes.string,
  }),
};

export default GenreForm;
