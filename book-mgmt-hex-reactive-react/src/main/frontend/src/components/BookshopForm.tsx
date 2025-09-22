import { useEffect, useState, type FormEvent, type ChangeEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import type { Bookshop } from '../types/bookshop';
import SuccessModal from '../components/SuccessModal';

interface BookshopFormProps {
	bookshop: Bookshop | null;
}

const BookshopForm: React.FC<BookshopFormProps> = ({ bookshop }) => {
	const [id, setId] = useState<string | null>(null);
	const [name, setName] = useState('');
	const navigate = useNavigate();
	const [showSuccessModal, setShowSuccessModal] = useState(false);
	const [isSuccessModalLoading, setIsSuccessModalLoading] = useState(false);

	useEffect(() => {
		if (bookshop) {
			setId(bookshop.bookshopId);
			setName(bookshop.name ?? '');
		} else {
			setId(null);
			setName('');
		}
	}, [bookshop]);

	const title = id ? (
		<h2 className="text-center">Edit Bookshop</h2>
	) : (
		<h2 className="text-center">Create Bookshop</h2>
	);

	const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
		e.preventDefault();

		const payload = { name };

		try {
			const response = await fetch(id ? `/bookshops/${id}` : '/bookshops', {
				method: id ? 'PUT' : 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify(payload),
			});

			if (response.ok) {
				const result = await response.json();
				console.log('Bookshop saved:', result);
				setShowSuccessModal(true);
			} else {
				const errorData = await response.json();
				console.error('Save error:', errorData);
				alert(errorData.message || 'Error saving Bookshop');
			}
		} catch (error) {
			console.error('Network Error:', error);
			alert('Network error while saving Bookshop');
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

				<button type="submit" className="btn btn-primary">Save</button>
			</form>
			<SuccessModal
				show={showSuccessModal}
				title={id ? 'Bookshop Updated' : 'Bookshop Created'}
				message={`The bookshop was successfully ${id ? 'updated' : 'created'}.`}
				isLoading={isSuccessModalLoading}
				onConfirm={() => {
					setIsSuccessModalLoading(true);
					setTimeout(() => {
						setIsSuccessModalLoading(false);
						setShowSuccessModal(false);
						navigate('/bookshops', { state: { refreshAfterWrite: true } });
					}, 1000);
				}}
			/>
		</div>
	);
};

export default BookshopForm;
