import { useEffect, useState, type FormEvent, type ChangeEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import type { Genre } from '../types/genre';
import SuccessModal from '../components/SuccessModal';

interface GenreFormProps {
	genre: Genre | null;
}

const GenreForm: React.FC<GenreFormProps> = ({ genre }) => {
	const [id, setId] = useState<string | null>(null);
	const [name, setName] = useState('');
	const [description, setDescription] = useState('');
	const navigate = useNavigate();
	const [showSuccessModal, setShowSuccessModal] = useState(false);
	const [isSuccessModalLoading, setIsSuccessModalLoading] = useState(false);

	useEffect(() => {
		if (genre) {
			setId(genre.genreId);
			setName(genre.name ?? '');
			setDescription(genre.description ?? '');
		}
	}, [genre]);

	const title = id ? (
		<h2 className="text-center">Edit Genres</h2>
	) : (
		<h2 className="text-center">Create Genres</h2>
	);

	const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
		e.preventDefault();
		
		const trimmedName = name.trim();
		const trimmedDescription = description.trim();

		const payload = {
			name: trimmedName,
			description: trimmedDescription,
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
				setShowSuccessModal(true);
			} else {
				const errorData = await response.json();
				console.error('Save error:', errorData);
				alert(errorData.message || 'Error saving Genre');
			}
		} catch (error) {
			console.error('Network Error:', error);
			alert('Network error while saving Genre');
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
						minLength={1}
						maxLength={50}
						required
						value={name}
						onChange={(e: ChangeEvent<HTMLInputElement>) => setName(e.target.value)}
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
						minLength={1}
						maxLength={100}
						required
						value={description}
						onChange={(e: ChangeEvent<HTMLInputElement>) => setDescription(e.target.value)}
					/>
					<small id="descHelp" className="form-text text-muted">
						A brief description for the literary genre
					</small>
				</div>

				<button type="submit" className="btn btn-primary">Save</button>
			</form>
			<SuccessModal
				show={showSuccessModal}
				title={id ? 'Genre Updated' : 'Genre Created'}
				message={`The genre was successfully ${id ? 'updated' : 'created'}.`}
				isLoading={isSuccessModalLoading}
				onConfirm={() => {
					setIsSuccessModalLoading(true);
					setTimeout(() => {
						setIsSuccessModalLoading(false);
						setShowSuccessModal(false);
						navigate('/genres', { state: { refreshAfterWrite: true } });
					}, 1000);
				}}
			/>
		</div>
	);
};

export default GenreForm;
