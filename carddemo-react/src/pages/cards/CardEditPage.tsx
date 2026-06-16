import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { cardApi } from '@/api';
import { ErrorMessage } from '@/components/common/ErrorMessage';
import type { Card } from '@/types';

export function CardEditPage() {
  const { num } = useParams<{ num: string }>();
  const navigate = useNavigate();
  const [card, setCard] = useState<Card | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (num) {
      cardApi
        .getByNumber(num)
        .then(setCard)
        .catch(() => setError('Card not found'));
    }
  }, [num]);

  const handleSave = async () => {
    if (!card || !num) return;
    setError(null);
    setSuccess(null);
    setLoading(true);
    try {
      await cardApi.update(num, card);
      setSuccess('Card updated successfully');
    } catch {
      setError('Failed to update card');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-2xl font-bold text-gray-800">Edit Card</h2>
        <button onClick={() => navigate(-1)} className="text-blue-600 hover:text-blue-800">
          &larr; Back
        </button>
      </div>

      <ErrorMessage message={error} />
      <ErrorMessage message={success} type="success" />

      {card && (
        <section className="bg-white rounded-lg shadow-sm border p-6 max-w-lg space-y-4">
          <div>
            <label className="block text-sm text-gray-600 mb-1">Card Number</label>
            <input type="text" value={card.cardNumber} disabled className="w-full px-3 py-2 bg-gray-100 border rounded-md" />
          </div>
          <div>
            <label className="block text-sm text-gray-600 mb-1">Account ID</label>
            <input type="text" value={card.accountId} disabled className="w-full px-3 py-2 bg-gray-100 border rounded-md" />
          </div>
          <div>
            <label className="block text-sm text-gray-600 mb-1">Status</label>
            <select
              value={card.activeStatus}
              onChange={(e) => setCard({ ...card, activeStatus: e.target.value })}
              className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500"
            >
              <option value="Y">Active</option>
              <option value="N">Inactive</option>
            </select>
          </div>
          <div className="flex gap-3 pt-4">
            <button onClick={handleSave} disabled={loading} className="px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-500 disabled:bg-gray-400">
              {loading ? 'Saving...' : 'Save'}
            </button>
            <button onClick={() => navigate(-1)} className="px-6 py-2 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50">
              Cancel
            </button>
          </div>
        </section>
      )}
    </div>
  );
}
