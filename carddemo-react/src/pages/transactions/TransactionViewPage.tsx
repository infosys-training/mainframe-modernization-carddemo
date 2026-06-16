import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { transactionApi } from '@/api';
import { ErrorMessage } from '@/components/common/ErrorMessage';
import type { Transaction } from '@/types';

function formatCurrency(value: number): string {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
  }).format(value);
}

export function TransactionViewPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [transaction, setTransaction] = useState<Transaction | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (id) {
      transactionApi
        .getById(id)
        .then(setTransaction)
        .catch(() => setError('Transaction not found'));
    }
  }, [id]);

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-2xl font-bold text-gray-800">Transaction Details</h2>
        <button onClick={() => navigate(-1)} className="text-blue-600 hover:text-blue-800">
          &larr; Back
        </button>
      </div>

      <ErrorMessage message={error} />

      {transaction && (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <section className="bg-white rounded-lg shadow-sm border p-6">
            <h3 className="text-lg font-semibold text-blue-800 mb-4">Transaction Information</h3>
            <dl className="grid grid-cols-2 gap-x-4 gap-y-3 text-sm">
              <dt className="text-gray-500">Transaction ID</dt>
              <dd className="font-medium">{transaction.transactionId}</dd>
              <dt className="text-gray-500">Card Number</dt>
              <dd className="font-medium">{transaction.cardNumber}</dd>
              <dt className="text-gray-500">Account ID</dt>
              <dd className="font-medium">{transaction.accountId}</dd>
              <dt className="text-gray-500">Type Code</dt>
              <dd className="font-medium">{transaction.typeCode}</dd>
              <dt className="text-gray-500">Category Code</dt>
              <dd className="font-medium">{transaction.categoryCode}</dd>
              <dt className="text-gray-500">Source</dt>
              <dd className="font-medium">{transaction.source}</dd>
              <dt className="text-gray-500">Description</dt>
              <dd className="font-medium col-span-2">{transaction.description}</dd>
              <dt className="text-gray-500">Amount</dt>
              <dd className="font-medium">{formatCurrency(transaction.amount)}</dd>
              <dt className="text-gray-500">Original Date</dt>
              <dd className="font-medium">{transaction.originalDate}</dd>
              <dt className="text-gray-500">Processed Date</dt>
              <dd className="font-medium">{transaction.processedDate}</dd>
            </dl>
          </section>

          <section className="bg-white rounded-lg shadow-sm border p-6">
            <h3 className="text-lg font-semibold text-blue-800 mb-4">Merchant Information</h3>
            <dl className="grid grid-cols-2 gap-x-4 gap-y-3 text-sm">
              <dt className="text-gray-500">Merchant ID</dt>
              <dd className="font-medium">{transaction.merchantId}</dd>
              <dt className="text-gray-500">Merchant Name</dt>
              <dd className="font-medium">{transaction.merchantName}</dd>
              <dt className="text-gray-500">Merchant City</dt>
              <dd className="font-medium">{transaction.merchantCity}</dd>
              <dt className="text-gray-500">Merchant Zip</dt>
              <dd className="font-medium">{transaction.merchantZip}</dd>
            </dl>
          </section>
        </div>
      )}
    </div>
  );
}
