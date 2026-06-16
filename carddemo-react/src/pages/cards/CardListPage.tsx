import { useState, type FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { cardApi } from '@/api';
import { DataTable } from '@/components/common/DataTable';
import { Pagination } from '@/components/common/Pagination';
import { ErrorMessage } from '@/components/common/ErrorMessage';
import type { Card } from '@/types';

export function CardListPage() {
  const navigate = useNavigate();
  const [accountId, setAccountId] = useState('');
  const [cardNumber, setCardNumber] = useState('');
  const [cards, setCards] = useState<Card[]>([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(0);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const fetchCards = async (page: number) => {
    setLoading(true);
    setError(null);
    try {
      const result = await cardApi.list({
        accountId: accountId ? Number(accountId) : undefined,
        cardNumber: cardNumber || undefined,
        page,
        size: 7,
      });
      setCards(result.data);
      setTotalPages(result.totalPages);
      setCurrentPage(result.currentPage);
    } catch {
      setError('Failed to fetch cards');
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (e: FormEvent) => {
    e.preventDefault();
    fetchCards(1);
  };

  const columns = [
    { key: 'accountId', header: 'Account Number' },
    { key: 'cardNumber', header: 'Card Number' },
    {
      key: 'activeStatus',
      header: 'Status',
      render: (card: Card) => (
        <span className={card.activeStatus === 'Y' ? 'text-green-600' : 'text-red-600'}>
          {card.activeStatus === 'Y' ? 'Active' : 'Inactive'}
        </span>
      ),
    },
  ];

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-2xl font-bold text-gray-800">Credit Card List</h2>
        <button onClick={() => navigate(-1)} className="text-blue-600 hover:text-blue-800">
          &larr; Back
        </button>
      </div>

      <form onSubmit={handleSearch} className="flex flex-wrap gap-3 mb-6">
        <input
          type="text"
          maxLength={11}
          value={accountId}
          onChange={(e) => setAccountId(e.target.value.replace(/\D/g, ''))}
          placeholder="Account Number (11 chars)"
          className="px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500"
        />
        <input
          type="text"
          maxLength={16}
          value={cardNumber}
          onChange={(e) => setCardNumber(e.target.value)}
          placeholder="Card Number (16 chars)"
          className="px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500"
        />
        <button type="submit" disabled={loading} className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-500 disabled:bg-gray-400">
          Search
        </button>
      </form>

      <ErrorMessage message={error} />

      <DataTable
        columns={columns}
        data={cards}
        keyExtractor={(card) => card.cardNumber}
        onRowClick={(card) => navigate(`/cards/${card.cardNumber}`)}
      />

      <Pagination currentPage={currentPage} totalPages={totalPages} onPageChange={fetchCards} />
    </div>
  );
}
