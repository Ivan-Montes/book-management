import { useEffect, useState, type ChangeEvent, type FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import type { BookBookshop } from '../types/bookBookshop';
import SuccessModal from '../components/SuccessModal'; 

interface BookBookshopFormProps {
  bookBookshop: BookBookshop | null;
}

const BookBookshopForm: React.FC<BookBookshopFormProps> = ({ bookBookshop }) => {
  const [bookId, setBookId] = useState('');
  const [isbn, setIsbn] = useState('');
  const [title, setTitle] = useState('');
  const [bookshopId, setBookshopId] = useState('');
  const [name, setName] = useState('');
  const [price, setPrice] = useState('');
  const [units, setUnits] = useState('');
  const [showSuccessModal, setShowSuccessModal] = useState(false);
  const [isSuccessModalLoading, setIsSuccessModalLoading] = useState(false);

  useEffect(() => {
    if (bookBookshop) {
      setBookId(bookBookshop.bookId.toString());
      setIsbn(bookBookshop.isbn ?? '');
      setTitle(bookBookshop.title ?? '');
      setBookshopId(bookBookshop.bookshopId.toString());
      setName(bookBookshop.name ?? '');
      setPrice(bookBookshop.price?.toString() ?? '');
      setUnits(bookBookshop.units?.toString() ?? '');
    }
  }, [bookBookshop]);

  const navigate = useNavigate();

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    const payload: BookBookshop = {
      bookId: Number(bookId),
      isbn,
      title,
      bookshopId: Number(bookshopId),
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
              min="0"
              max="999"
              step="0.01"
              required
              value={price}
              onChange={(e: ChangeEvent<HTMLInputElement>) => setPrice(e.target.value)}
            />
          </div>
          <div className="col-md-6">
            <label htmlFor="inputUnits" className="form-label">Units</label>
            <input
              type="number"
              className="form-control form-control-lg"
              id="inputUnits"
              min="0"
              max="99"
              required
              value={units}
              onChange={(e: ChangeEvent<HTMLInputElement>) => setUnits(e.target.value)}
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
		  <SuccessModal
		    show={showSuccessModal}
		    title={'Edit Book in Bookshop'}
		    message={`The edition was successfully.`}
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

export default BookBookshopForm;
