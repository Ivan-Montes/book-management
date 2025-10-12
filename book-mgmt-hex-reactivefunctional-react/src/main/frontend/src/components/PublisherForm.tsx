import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import type { Publisher } from '../types/publisher';
import SuccessModal from '../components/SuccessModal'; 

interface PublisherFormProps {
  publisher?: Publisher | null;
}

const PublisherForm: React.FC<PublisherFormProps> = ({ publisher }) => {
  const [id, setId] = useState<string | ''>('');
  const [name, setName] = useState('');
  const [showSuccessModal, setShowSuccessModal] = useState(false);
  const [isSuccessModalLoading, setIsSuccessModalLoading] = useState(false);

  const navigate = useNavigate();

  useEffect(() => {
    if (publisher) {
      setId(publisher.publisherId ?? '');
      setName(publisher.name ?? '');
    }
  }, [publisher]);

  const title = id ? (
    <h2 className="text-center">Edit Publisher</h2>
  ) : (
    <h2 className="text-center">Create Publisher</h2>
  );

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    const payload = { name: name.trim() };

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
		setShowSuccessModal(true); 
      } else {
        const errorData = await response.json();
        console.error('Save error:', errorData);
        alert(errorData.message || 'Error saving Publisher');
      }
    } catch (error) {
      console.error('Network Error:', error);
      alert('Network error while saving Publisher');
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
            minLength={1}
            maxLength={50}
            required
            value={name}
            onChange={(e) => setName(e.target.value)}
          />
        </div>

        <button type="submit" className="btn btn-primary">Save</button>
      </form>
	  <SuccessModal
	    show={showSuccessModal}
	    title={id ? 'Publisher Updated' : 'Publisher Created'}
	    message={`The publisher was successfully ${id ? 'updated' : 'created'}.`}
	    isLoading={isSuccessModalLoading}
	    onConfirm={() => {
	      setIsSuccessModalLoading(true);
	      setTimeout(() => {
	        setIsSuccessModalLoading(false);
	        setShowSuccessModal(false);
	        navigate('/publishers', { state: { refreshAfterWrite: true } });
	      }, 1000);
	    }}
	  />
    </div>
  );
};

export default PublisherForm;
