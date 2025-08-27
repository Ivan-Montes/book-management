import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import PublisherForm from '../components/PublisherForm';
import type { Publisher } from '../types/publisher';

const PublisherFormContainer: React.FC = () => {
  const [publisher, setPublisher] = useState<Publisher | null>(null);
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  useEffect(() => {
    if (id) {
      fetch(`/publishers/${id}`)
        .then((res) => {
          if (!res.ok) throw new Error('Error fetching publisher');
          return res.json();
        })
        .then((data: Publisher) => setPublisher(data))
        .catch((err) => {
          console.error(err);
          alert('Error loading publisher');
          navigate('/publishers');
        });
    }
  }, [id, navigate]);

  return <PublisherForm publisher={publisher} />;
};

export default PublisherFormContainer;
