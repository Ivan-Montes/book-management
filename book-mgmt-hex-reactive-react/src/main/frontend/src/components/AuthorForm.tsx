import { useEffect, useState, type FormEvent, type ChangeEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import type { Author } from '../types/author';
import SuccessModal from '../components/SuccessModal';

interface AuthorFormProps {
	author: Author | null;
}

const AuthorForm: React.FC<AuthorFormProps> = ({ author }) => {
	const [id, setId] = useState<string | null>(null);
	const [name, setName] = useState('');
	const [surname, setSurname] = useState('');
	const navigate = useNavigate();
	const [showSuccessModal, setShowSuccessModal] = useState(false);
	const [isSuccessModalLoading, setIsSuccessModalLoading] = useState(false);

	useEffect(() => {
		if (author) {
			setId(author.authorId);
			setName(author.name ?? '');
			setSurname(author.surname ?? '');
		}
	}, [author]);

	const title = id ? (
		<h2 className="text-center">Edit Author</h2>
	) : (
		<h2 className="text-center">Create Author</h2>
	);

	const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
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
				setShowSuccessModal(true);
			} else {
				const errorData = await response.json();
				console.error('Save error:', errorData);
				alert(errorData.message || 'Error saving Author');
			}
		} catch (error) {
			console.error('Network Error:', error);
			alert('Network error while saving Author');
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
						pattern="^[a-zA-ZñÑáéíóúÁÉÍÓÚ]+[a-zA-ZñÑáéíóúÁÉÍÓÚ\s\\-\\.,&:]*"
						minLength={1}
						maxLength={50}
						required
						value={name}
						onChange={(e: ChangeEvent<HTMLInputElement>) => setName(e.target.value)}
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
						pattern="^[a-zA-ZñÑáéíóúÁÉÍÓÚ]+[a-zA-ZñÑáéíóúÁÉÍÓÚ\s\\-\\.,&:]*"
						minLength={1}
						maxLength={50}
						required
						value={surname}
						onChange={(e: ChangeEvent<HTMLInputElement>) => setSurname(e.target.value)}
					/>
				</div>

				<button type="submit" className="btn btn-primary">Save</button>
			</form>
			<SuccessModal
				show={showSuccessModal}
				title={id ? 'Author Updated' : 'Author Created'}
				message={`The author was successfully ${id ? 'updated' : 'created'}.`}
				isLoading={isSuccessModalLoading}
				onConfirm={() => {
					setIsSuccessModalLoading(true);
					setTimeout(() => {
						setIsSuccessModalLoading(false);
						setShowSuccessModal(false);
						navigate('/authors', { state: { refreshAfterWrite: true } });
					}, 1000);
				}}
			/>
		</div>
	);
};

export default AuthorForm;
