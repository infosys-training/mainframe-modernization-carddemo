import { useState, type FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { billingApi, accountApi } from '@/api';
import { ErrorMessage } from '@/components/common/ErrorMessage';
import { ConfirmDialog } from '@/components/common/ConfirmDialog';

function formatCurrency(value: number): string {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
  }).format(value);
}

export function BillPaymentPage() {
  const navigate = useNavigate();
  const [accountId, setAccountId] = useState('');
  const [currentBalance, setCurrentBalance] = useState<number | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);

  const handleLookup = async (e: FormEvent) => {
    e.preventDefault();
    if (!accountId.trim()) {
      setError('Please enter an Account ID');
      return;
    }
    setError(null);
    setSuccess(null);
    setLoading(true);
    try {
      const account = await accountApi.getById(Number(accountId));
      setCurrentBalance(account.currentBalance);
    } catch {
      setError('Account not found');
      setCurrentBalance(null);
    } finally {
      setLoading(false);
    }
  };

  const handleConfirm = async () => {
    setShowConfirm(false);
    setLoading(true);
    setError(null);
    try {
      const result = await billingApi.pay({ accountId: Number(accountId) });
      setSuccess(result.message || 'Payment processed successfully');
      setCurrentBalance(0);
    } catch {
      setError('Payment failed');
    } finally {
      setLoading(false);
    }
  };

  const handleClear = () => {
    setAccountId('');
    setCurrentBalance(null);
    setError(null);
    setSuccess(null);
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-2xl font-bold text-gray-800">Bill Payment</h2>
        <button onClick={() => navigate(-1)} className="text-blue-600 hover:text-blue-800">
          &larr; Back
        </button>
      </div>

      <ErrorMessage message={error} />
      <ErrorMessage message={success} type="success" />

      <div className="bg-white rounded-lg shadow-sm border p-6 max-w-lg space-y-6">
        <form onSubmit={handleLookup} className="flex gap-3">
          <input
            type="text"
            maxLength={11}
            value={accountId}
            onChange={(e) => setAccountId(e.target.value.replace(/\D/g, ''))}
            placeholder="Account ID (11 digits)"
            className="flex-1 px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500"
          />
          <button type="submit" disabled={loading} className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-500 disabled:bg-gray-400">
            Lookup
          </button>
        </form>

        {currentBalance !== null && (
          <div className="p-4 bg-gray-50 rounded-md">
            <p className="text-sm text-gray-600">Current Balance</p>
            <p className="text-2xl font-bold text-gray-800">{formatCurrency(currentBalance)}</p>
          </div>
        )}

        <div className="flex gap-3">
          {currentBalance !== null && currentBalance > 0 && (
            <button
              onClick={() => setShowConfirm(true)}
              disabled={loading}
              className="px-6 py-2 bg-green-600 text-white rounded-md hover:bg-green-500 disabled:bg-gray-400"
            >
              Pay Balance
            </button>
          )}
          <button onClick={handleClear} className="px-6 py-2 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50">
            Clear
          </button>
        </div>
      </div>

      <ConfirmDialog
        open={showConfirm}
        title="Confirm Payment"
        message="Do you want to pay your balance now?"
        onConfirm={handleConfirm}
        onCancel={() => setShowConfirm(false)}
      />
    </div>
  );
}
