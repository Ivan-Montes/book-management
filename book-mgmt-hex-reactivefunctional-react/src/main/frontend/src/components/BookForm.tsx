import { useState, useEffect, type FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthorModal from './AuthorModal';
import GenreModal from './GenreModal';
import PublisherModal from './PublisherModal';
import SuccessModal from '../components/SuccessModal';

import type { Book } from '../types/book';
import type { Author } from '../types/author';
import type { Publisher } from '../types/publisher';
import type { Genre } from '../types/genre';

interface BookFormProps {
	book?: Book | null;
}

const BookForm: React.FC<BookFormProps> = ({ book }) => {
	const [isbn, setIsbn] = useState('');
	const [title, setTitle] = useState('');
	const [publisher, setPublisher] = useState<Publisher | null>(null);
	const [genre, setGenre] = useState<Genre | null>(null);
	const [authors, setAuthors] = useState<Author[]>([]);

	const [showPublisherModal, setShowPublisherModal] = useState(false);
	const [showGenreModal, setShowGenreModal] = useState(false);
	const [showAuthorModal, setShowAuthorModal] = useState(false);

	const [showSuccessModal, setShowSuccessModal] = useState(false);
	const [isSuccessModalLoading, setIsSuccessModalLoading] = useState(false);

	const navigate = useNavigate();

	useEffect(() => {
		if (book) {
			setIsbn(book.isbn ?? '');
			setTitle(book.title ?? '');
			setPublisher(
				book.publisherId && book.publisherName
					? { publisherId: book.publisherId, name: book.publisherName }
					: null
			);
			setGenre(
				book.genreId && book.genreName
					? { genreId: book.genreId, name: book.genreName }
					: null
			);
			setAuthors(book.authors ?? []);
		}
	}, [book]);

	const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
		e.preventDefault();

		if (!publisher || !genre || authors.length === 0) {
			alert('Please select publisher, genre and at least one author');
			return;
		}

		const payload = {
			isbn,
			title: title.trim(),
			publisherId: publisher.publisherId,
			genreId: genre.genreId,
			authorsId: authors.map((a) => a.authorId),
			authors: authors,
		};

		try {
			const response = await fetch(book ? `/books/${book.bookId}` : '/books', {
				method: book ? 'PUT' : 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify(payload),
			});

			if (response.ok) {
				const result = await response.json();
				console.log('Book saved:', result);
				setShowSuccessModal(true);
			} else {
				const errorData = await response.json();
				console.error('Save error:', errorData);
				alert(errorData.message || 'Error saving Book');
			}
		} catch (error) {
			console.error('Network Error:', error);
			alert('Network error while saving Book');
		}
	};

	return (
		<div className="container">
			<h2 className="text-center">{book ? 'Edit Book' : 'Create Book'}</h2>

			<form onSubmit={handleSubmit}>
				<div className="form-group mb-3">
					<label htmlFor="inputIsbn" className="form-label">ISBN</label>
					<input
						type="text"
						className="form-control"
						id="inputIsbn"
						placeholder="Enter ISBN"
						name="isbn"
						pattern="[\d]{10,13}"
						minLength={10}
						maxLength={13}
						required
						value={isbn}
						onChange={(e) => setIsbn(e.target.value)}
					/>
				</div>

				<div className="form-group mb-3">
					<label htmlFor="inputTitle" className="form-label">Title</label>
					<input
						type="text"
						className="form-control"
						id="inputTitle"
						placeholder="Enter Title"
						name="title"
						pattern="^[a-zA-ZñÑáéíóúÁÉÍÓÚ¿¡]+[a-zA-Z0-9ñÑáéíóúÁÉÍÓÚ\s\-\?¿\!¡\.&,:]*"
						minLength={1}
						maxLength={100}
						required
						value={title}
						onChange={(e) => setTitle(e.target.value)}
					/>
				</div>

				<div className="form-group mb-3">
					<label className="form-label">Publisher</label>
					<button
						type="button"
						className="form-control text-start"
						onClick={() => setShowPublisherModal(true)}
					>
						{publisher?.name || 'Select publisher'}
					</button>
				</div>

				<div className="form-group mb-3">
					<label className="form-label">Genre</label>
					<button
						type="button"
						className="form-control text-start"
						onClick={() => setShowGenreModal(true)}
					>
						{genre?.name || 'Select genre'}
					</button>
				</div>

				<div className="form-group mb-3">
					<label className="form-label">Authors</label>
					<button
						type="button"
						className="form-control text-start"
						onClick={() => setShowAuthorModal(true)}
					>
						{authors.length > 0
							? authors.map((a) => `${a.name} ${a.surname}`).join(', ')
							: 'Select authors'}
					</button>
					<div className="mt-2">
						{authors.map((author) => (
							<span key={author.authorId} className="badge bg-secondary me-1">
								{author.name} {author.surname}
							</span>
						))}
					</div>
				</div>

				<button type="submit" className="btn btn-primary">Save</button>
			</form>

			{showPublisherModal && (
				<PublisherModal
					selected={publisher}
					onSelect={(pub) => {
						setPublisher(pub);
						setShowPublisherModal(false);
					}}
					onClose={() => setShowPublisherModal(false)}
				/>
			)}

			{showGenreModal && (
				<GenreModal
					selected={genre}
					onSelect={(gen) => {
						setGenre(gen);
						setShowGenreModal(false);
					}}
					onClose={() => setShowGenreModal(false)}
				/>
			)}

			{showAuthorModal && (
				<AuthorModal
					selected={authors}
					onSelect={(auths) => {
						setAuthors(auths);
						setShowAuthorModal(false);
					}}
					onClose={() => setShowAuthorModal(false)}
				/>
			)}

			<SuccessModal
				show={showSuccessModal}
				title={book ? 'Book Updated' : 'Book Created'}
				message={`The book was successfully ${book ? 'updated' : 'created'}.`}
				isLoading={isSuccessModalLoading}
				onConfirm={() => {
					setIsSuccessModalLoading(true);
					setTimeout(() => {
						setIsSuccessModalLoading(false);
						setShowSuccessModal(false);
						navigate('/books', { state: { refreshAfterWrite: true } });
					}, 1000);
				}}
			/>
		</div>
	);
};

export default BookForm;
