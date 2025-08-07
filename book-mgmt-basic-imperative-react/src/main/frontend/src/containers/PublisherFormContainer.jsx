import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import PublisherForm from '../components/PublisherForm';

function PublisherFormContainer() {
  const [publisher, setPublisher] = useState(null);
  const { id } = useParams();
  const navigate = useNavigate();

  useEffect(() => {
    if (id) {
      fetch(`/publishers/${id}`)
        .then(res => {
          if (!res.ok) throw new Error('Error fetching publisher');
          return res.json();
        })
        .then(data => setPublisher(data))
        .catch(err => {
          console.error(err);
          alert('Error loading publisher');
          navigate('/publishers');
        });
    }
  }, [id, navigate]);

  return (
    <PublisherForm publisher={publisher} />
  );
}

export default PublisherFormContainer;
