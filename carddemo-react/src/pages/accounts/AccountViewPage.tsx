import { useState, type FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { accountApi } from '@/api';
import { ErrorMessage } from '@/components/common/ErrorMessage';
import type { Account } from '@/types';

function formatCurrency(value: number): string {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
    minimumFractionDigits: 2,
  }).format(value);
}

export function AccountViewPage() {
  const [accountId, setAccountId] = useState('');
  const [account, setAccount] = useState<Account | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSearch = async (e: FormEvent) => {
    e.preventDefault();
    if (!accountId.trim()) {
      setError('Please enter an Account Number');
      return;
    }
    setError(null);
    setLoading(true);
    try {
      const data = await accountApi.getById(Number(accountId));
      setAccount(data);
    } catch {
      setError('Account not found or an error occurred');
      setAccount(null);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-2xl font-bold text-gray-800">Account View</h2>
        <button onClick={() => navigate(-1)} className="text-blue-600 hover:text-blue-800">
          &larr; Back
        </button>
      </div>

      <form onSubmit={handleSearch} className="flex gap-3 mb-6">
        <input
          type="text"
          maxLength={11}
          value={accountId}
          onChange={(e) => setAccountId(e.target.value.replace(/\D/g, ''))}
          placeholder="Enter Account Number (11 digits)"
          className="flex-1 max-w-xs px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500"
        />
        <button
          type="submit"
          disabled={loading}
          className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-500 disabled:bg-gray-400"
        >
          {loading ? 'Loading...' : 'View'}
        </button>
      </form>

      <ErrorMessage message={error} />

      {account && (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <section className="bg-white rounded-lg shadow-sm border p-6">
            <h3 className="text-lg font-semibold text-blue-800 mb-4">Account Information</h3>
            <dl className="grid grid-cols-2 gap-x-4 gap-y-3 text-sm">
              <dt className="text-gray-500">Account ID</dt>
              <dd className="font-medium">{account.accountId}</dd>
              <dt className="text-gray-500">Status</dt>
              <dd className="font-medium">{account.activeStatus === 'Y' ? 'Active' : 'Inactive'}</dd>
              <dt className="text-gray-500">Opened Date</dt>
              <dd className="font-medium">{account.openDate}</dd>
              <dt className="text-gray-500">Expiry Date</dt>
              <dd className="font-medium">{account.expiryDate}</dd>
              <dt className="text-gray-500">Reissue Date</dt>
              <dd className="font-medium">{account.reissueDate}</dd>
              <dt className="text-gray-500">Credit Limit</dt>
              <dd className="font-medium">{formatCurrency(account.creditLimit)}</dd>
              <dt className="text-gray-500">Cash Credit Limit</dt>
              <dd className="font-medium">{formatCurrency(account.cashCreditLimit)}</dd>
              <dt className="text-gray-500">Current Balance</dt>
              <dd className="font-medium">{formatCurrency(account.currentBalance)}</dd>
              <dt className="text-gray-500">Cycle Credit</dt>
              <dd className="font-medium">{formatCurrency(account.currentCycleCredit)}</dd>
              <dt className="text-gray-500">Cycle Debit</dt>
              <dd className="font-medium">{formatCurrency(account.currentCycleDebit)}</dd>
              <dt className="text-gray-500">Account Group</dt>
              <dd className="font-medium">{account.accountGroup}</dd>
            </dl>
          </section>

          <section className="bg-white rounded-lg shadow-sm border p-6">
            <h3 className="text-lg font-semibold text-blue-800 mb-4">Customer Information</h3>
            <dl className="grid grid-cols-2 gap-x-4 gap-y-3 text-sm">
              <dt className="text-gray-500">Customer ID</dt>
              <dd className="font-medium">{account.customerId}</dd>
              <dt className="text-gray-500">Name</dt>
              <dd className="font-medium">{account.firstName} {account.middleName} {account.lastName}</dd>
              <dt className="text-gray-500">SSN</dt>
              <dd className="font-medium">{account.customerSSN}</dd>
              <dt className="text-gray-500">Date of Birth</dt>
              <dd className="font-medium">{account.customerDOB}</dd>
              <dt className="text-gray-500">FICO Score</dt>
              <dd className="font-medium">{account.customerFICO}</dd>
              <dt className="text-gray-500">Address</dt>
              <dd className="font-medium">{account.addressLine1}</dd>
              <dt className="text-gray-500">Address Line 2</dt>
              <dd className="font-medium">{account.addressLine2}</dd>
              <dt className="text-gray-500">City/State/Zip</dt>
              <dd className="font-medium">{account.city}, {account.state} {account.zipCode}</dd>
              <dt className="text-gray-500">Country</dt>
              <dd className="font-medium">{account.country}</dd>
              <dt className="text-gray-500">Phone 1</dt>
              <dd className="font-medium">{account.phone1}</dd>
              <dt className="text-gray-500">Phone 2</dt>
              <dd className="font-medium">{account.phone2}</dd>
              <dt className="text-gray-500">Government ID</dt>
              <dd className="font-medium">{account.governmentId}</dd>
              <dt className="text-gray-500">EFT Account</dt>
              <dd className="font-medium">{account.eftAccountId}</dd>
              <dt className="text-gray-500">Primary Card Holder</dt>
              <dd className="font-medium">{account.primaryCardHolder ? 'Yes' : 'No'}</dd>
            </dl>
          </section>
        </div>
      )}
    </div>
  );
}
