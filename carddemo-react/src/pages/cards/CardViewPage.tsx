import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { cardApi } from '@/api';
import { ErrorMessage } from '@/components/common/ErrorMessage';
import type { Card } from '@/types';

export function CardViewPage() {
  const { num } = useParams<{ num: string }>();
  const navigate = useNavigate();
  const [card, setCard] = useState<Card | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (num) {
      cardApi
        .getByNumber(num)
        .then(setCard)
        .catch(() => setError('Card not found'));
    }
  }, [num]);

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-2xl font-bold text-gray-800">Card Details</h2>
        <div className="flex gap-3">
          <button onClick={() => navigate(`/cards/${num}/edit`)} className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-500">
            Edit
          </button>
          <button onClick={() => navigate(-1)} className="text-blue-600 hover:text-blue-800">
            &larr; Back
          </button>
        </div>
      </div>

      <ErrorMessage message={error} />

      {card && (
        <section className="bg-white rounded-lg shadow-sm border p-6 max-w-lg">
          <dl className="grid grid-cols-2 gap-x-4 gap-y-3 text-sm">
            <dt className="text-gray-500">Card Number</dt>
            <dd className="font-medium">{card.cardNumber}</dd>
            <dt className="text-gray-500">Account ID</dt>
            <dd className="font-medium">{card.accountId}</dd>
            <dt className="text-gray-500">Status</dt>
            <dd className={`font-medium ${card.activeStatus === 'Y' ? 'text-green-600' : 'text-red-600'}`}>
              {card.activeStatus === 'Y' ? 'Active' : 'Inactive'}
            </dd>
          </dl>
        </section>
      )}
    </div>
  );
}
