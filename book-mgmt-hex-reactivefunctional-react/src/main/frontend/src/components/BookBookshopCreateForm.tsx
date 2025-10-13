import { useState, type FormEvent, type ChangeEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import type { Book } from '../types/book';
import type { Bookshop } from '../types/bookshop';
import type { PaginatedResult } from '../types/pagination';
import SuccessModal from '../components/SuccessModal';

interface BookBookshopCreateFormProps {
	booksPaginated: PaginatedResult<Book>;
	bookshopsPaginated: PaginatedResult<Bookshop>;
}

const BookBookshopCreateForm: React.FC<BookBookshopCreateFormProps> = ({
	booksPaginated,
	bookshopsPaginated,
}) => {
	const books = Array.isArray(booksPaginated?.content) ? booksPaginated.content : [];
	const bookshops = Array.isArray(bookshopsPaginated?.content) ? bookshopsPaginated.content : [];

	const [bookId, setBookId] = useState('');
	const [bookshopId, setBookshopId] = useState('');
	const [price, setPrice] = useState('');
	const [units, setUnits] = useState('');

	const [showSuccessModal, setShowSuccessModal] = useState(false);
	const [isSuccessModalLoading, setIsSuccessModalLoading] = useState(false);
	const navigate = useNavigate();

	const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
		e.preventDefault();
		if (!bookId || !bookshopId || price === '' || units === '') {
			alert('All fields are required');
			return;
		}
		
		const formData = {
			bookId: String(bookId),
			bookshopId: String(bookshopId),
			price: Number(price),
			units: Number(units),
		};

		try {
			const response = await fetch('/bookbookshops', {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify(formData),
			});

			if (response.ok) {
				const result = await response.json();
				console.log('BookBookshop saved:', result);
				setShowSuccessModal(true);
			} else {
				const errorData = await response.json();
				console.error('Save error:', errorData);
				alert(errorData.message || 'Error saving BookBookshop');
			}
		} catch (error) {
			console.error('Network Error:', error);
			alert('Network error while saving BookBookshop');
		}

	};

	return (
		<div className="container mt-3">
			<h2 className="text-center">Add Book to Bookshop</h2>
			<form onSubmit={handleSubmit}>
				<div className="row">
					<div className="col-md-6 mb-3">
						<label htmlFor="bookSelect" className="form-label">Select Book</label>
						<select
							className="form-select"
							id="bookSelect"
							value={bookId}
							onChange={(e: ChangeEvent<HTMLSelectElement>) => setBookId(e.target.value)}
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
							onChange={(e: ChangeEvent<HTMLSelectElement>) => setBookshopId(e.target.value)}
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
							onChange={(e: ChangeEvent<HTMLInputElement>) => setPrice(e.target.value)}
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
							onChange={(e: ChangeEvent<HTMLInputElement>) => setUnits(e.target.value)}
							required
						/>
					</div>
				</div>

				<div className="text-center mt-3">
					<button type="submit" className="btn btn-success">Save</button>
				</div>
			</form>
			<SuccessModal
				show={showSuccessModal}
				title={'Add Book in Bookshop'}
				message={`The addition was successfully.`}
				isLoading={isSuccessModalLoading}
				onConfirm={() => {
					setIsSuccessModalLoading(true);
					setTimeout(() => {
						setIsSuccessModalLoading(false);
						setShowSuccessModal(false);
						navigate('/bookbookshops', { state: { refreshAfterWrite: true } });
					}, 1000);
				}}
			/>
		</div>
	);
};

export default BookBookshopCreateForm;
