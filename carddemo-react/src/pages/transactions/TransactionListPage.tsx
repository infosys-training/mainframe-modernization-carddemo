import { useState, type FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { transactionApi } from '@/api';
import { DataTable } from '@/components/common/DataTable';
import { Pagination } from '@/components/common/Pagination';
import { ErrorMessage } from '@/components/common/ErrorMessage';
import type { Transaction } from '@/types';

function formatCurrency(value: number): string {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
  }).format(value);
}

export function TransactionListPage() {
  const navigate = useNavigate();
  const [tranId, setTranId] = useState('');
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(0);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const fetchTransactions = async (page: number) => {
    setLoading(true);
    setError(null);
    try {
      const result = await transactionApi.list({
        tranId: tranId || undefined,
        page,
        size: 10,
      });
      setTransactions(result.data);
      setTotalPages(result.totalPages);
      setCurrentPage(result.currentPage);
    } catch {
      setError('Failed to fetch transactions');
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (e: FormEvent) => {
    e.preventDefault();
    fetchTransactions(1);
  };

  const columns = [
    { key: 'transactionId', header: 'Transaction ID' },
    { key: 'originalDate', header: 'Date' },
    { key: 'description', header: 'Description' },
    {
      key: 'amount',
      header: 'Amount',
      render: (txn: Transaction) => (
        <span className={txn.amount < 0 ? 'text-red-600' : 'text-green-600'}>
          {formatCurrency(txn.amount)}
        </span>
      ),
    },
  ];

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-2xl font-bold text-gray-800">Transaction List</h2>
        <button onClick={() => navigate(-1)} className="text-blue-600 hover:text-blue-800">
          &larr; Back
        </button>
      </div>

      <form onSubmit={handleSearch} className="flex gap-3 mb-6">
        <input
          type="text"
          maxLength={16}
          value={tranId}
          onChange={(e) => setTranId(e.target.value)}
          placeholder="Transaction ID (16 chars)"
          className="flex-1 max-w-sm px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500"
        />
        <button type="submit" disabled={loading} className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-500 disabled:bg-gray-400">
          Search
        </button>
      </form>

      <ErrorMessage message={error} />

      <DataTable
        columns={columns}
        data={transactions}
        keyExtractor={(txn) => txn.transactionId}
        onRowClick={(txn) => navigate(`/transactions/${txn.transactionId}`)}
      />

      <Pagination currentPage={currentPage} totalPages={totalPages} onPageChange={fetchTransactions} />
    </div>
  );
}
