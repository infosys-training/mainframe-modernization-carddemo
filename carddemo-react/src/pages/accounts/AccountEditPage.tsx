import { useState, useEffect, useCallback, type FormEvent } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { accountApi } from '@/api';
import { ErrorMessage } from '@/components/common/ErrorMessage';
import type { Account } from '@/types';

export function AccountEditPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [accountId, setAccountId] = useState(id === '0' ? '' : id || '');
  const [account, setAccount] = useState<Account | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const loadAccount = useCallback(async (acctId: number, signal?: AbortSignal) => {
    setLoading(true);
    setError(null);
    try {
      const data = await accountApi.getById(acctId);
      if (!signal?.aborted) {
        setAccount(data);
        setAccountId(String(data.accountId));
      }
    } catch {
      if (!signal?.aborted) setError('Account not found');
    } finally {
      if (!signal?.aborted) setLoading(false);
    }
  }, []);

  useEffect(() => {
    if (!id || id === '0') return;
    const controller = new AbortController();
    // eslint-disable-next-line react-hooks/set-state-in-effect -- initial data fetch on mount
    loadAccount(Number(id), controller.signal);
    return () => controller.abort();
  }, [id, loadAccount]);

  const handleSearch = (e: FormEvent) => {
    e.preventDefault();
    if (accountId.trim()) {
      loadAccount(Number(accountId));
    }
  };

  const handleFieldChange = (field: keyof Account, value: string | number | boolean) => {
    if (account) {
      setAccount({ ...account, [field]: value });
    }
  };

  const handleSave = async () => {
    if (!account) return;
    setError(null);
    setSuccess(null);
    setLoading(true);
    try {
      await accountApi.update(account.accountId, account);
      setSuccess('Account updated successfully');
    } catch {
      setError('Failed to update account');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-2xl font-bold text-gray-800">Account Update</h2>
        <button onClick={() => navigate(-1)} className="text-blue-600 hover:text-blue-800">
          &larr; Back
        </button>
      </div>

      {!account && (
        <form onSubmit={handleSearch} className="flex gap-3 mb-6">
          <input
            type="text"
            maxLength={11}
            value={accountId}
            onChange={(e) => setAccountId(e.target.value.replace(/\D/g, ''))}
            placeholder="Enter Account Number (11 digits)"
            className="flex-1 max-w-xs px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500"
          />
          <button type="submit" disabled={loading} className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-500">
            Load
          </button>
        </form>
      )}

      <ErrorMessage message={error} />
      <ErrorMessage message={success} type="success" />

      {account && (
        <div className="space-y-6">
          <section className="bg-white rounded-lg shadow-sm border p-6">
            <h3 className="text-lg font-semibold text-blue-800 mb-4">Account Information</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              <div>
                <label className="block text-sm text-gray-600 mb-1">Account ID</label>
                <input type="text" value={account.accountId} disabled className="w-full px-3 py-2 bg-gray-100 border rounded-md" />
              </div>
              <div>
                <label className="block text-sm text-gray-600 mb-1">Status</label>
                <select
                  value={account.activeStatus}
                  onChange={(e) => handleFieldChange('activeStatus', e.target.value)}
                  className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500"
                >
                  <option value="Y">Active</option>
                  <option value="N">Inactive</option>
                </select>
              </div>
              <div>
                <label className="block text-sm text-gray-600 mb-1">Open Date (YYYY-MM-DD)</label>
                <input
                  type="date"
                  value={account.openDate}
                  onChange={(e) => handleFieldChange('openDate', e.target.value)}
                  className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500"
                />
              </div>
              <div>
                <label className="block text-sm text-gray-600 mb-1">Expiry Date (YYYY-MM-DD)</label>
                <input
                  type="date"
                  value={account.expiryDate}
                  onChange={(e) => handleFieldChange('expiryDate', e.target.value)}
                  className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500"
                />
              </div>
              <div>
                <label className="block text-sm text-gray-600 mb-1">Credit Limit</label>
                <input
                  type="number"
                  step="0.01"
                  value={account.creditLimit}
                  onChange={(e) => handleFieldChange('creditLimit', Number(e.target.value))}
                  className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500"
                />
              </div>
              <div>
                <label className="block text-sm text-gray-600 mb-1">Cash Credit Limit</label>
                <input
                  type="number"
                  step="0.01"
                  value={account.cashCreditLimit}
                  onChange={(e) => handleFieldChange('cashCreditLimit', Number(e.target.value))}
                  className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500"
                />
              </div>
              <div>
                <label className="block text-sm text-gray-600 mb-1">Account Group</label>
                <input
                  type="text"
                  value={account.accountGroup}
                  onChange={(e) => handleFieldChange('accountGroup', e.target.value)}
                  className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500"
                />
              </div>
            </div>
          </section>

          <section className="bg-white rounded-lg shadow-sm border p-6">
            <h3 className="text-lg font-semibold text-blue-800 mb-4">Customer Information</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              <div>
                <label className="block text-sm text-gray-600 mb-1">First Name</label>
                <input type="text" maxLength={25} value={account.firstName} onChange={(e) => handleFieldChange('firstName', e.target.value)} className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500" />
              </div>
              <div>
                <label className="block text-sm text-gray-600 mb-1">Middle Name</label>
                <input type="text" maxLength={25} value={account.middleName} onChange={(e) => handleFieldChange('middleName', e.target.value)} className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500" />
              </div>
              <div>
                <label className="block text-sm text-gray-600 mb-1">Last Name</label>
                <input type="text" maxLength={25} value={account.lastName} onChange={(e) => handleFieldChange('lastName', e.target.value)} className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500" />
              </div>
              <div>
                <label className="block text-sm text-gray-600 mb-1">Address Line 1</label>
                <input type="text" maxLength={50} value={account.addressLine1} onChange={(e) => handleFieldChange('addressLine1', e.target.value)} className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500" />
              </div>
              <div>
                <label className="block text-sm text-gray-600 mb-1">Address Line 2</label>
                <input type="text" maxLength={50} value={account.addressLine2} onChange={(e) => handleFieldChange('addressLine2', e.target.value)} className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500" />
              </div>
              <div>
                <label className="block text-sm text-gray-600 mb-1">City</label>
                <input type="text" maxLength={30} value={account.city} onChange={(e) => handleFieldChange('city', e.target.value)} className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500" />
              </div>
              <div>
                <label className="block text-sm text-gray-600 mb-1">State</label>
                <input type="text" maxLength={2} value={account.state} onChange={(e) => handleFieldChange('state', e.target.value.toUpperCase())} className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500" />
              </div>
              <div>
                <label className="block text-sm text-gray-600 mb-1">Zip Code</label>
                <input type="text" maxLength={10} value={account.zipCode} onChange={(e) => handleFieldChange('zipCode', e.target.value)} className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500" />
              </div>
              <div>
                <label className="block text-sm text-gray-600 mb-1">Country</label>
                <input type="text" maxLength={3} value={account.country} onChange={(e) => handleFieldChange('country', e.target.value)} className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500" />
              </div>
              <div>
                <label className="block text-sm text-gray-600 mb-1">Phone 1</label>
                <input type="text" maxLength={15} value={account.phone1} onChange={(e) => handleFieldChange('phone1', e.target.value)} className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500" />
              </div>
              <div>
                <label className="block text-sm text-gray-600 mb-1">Phone 2</label>
                <input type="text" maxLength={15} value={account.phone2} onChange={(e) => handleFieldChange('phone2', e.target.value)} className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500" />
              </div>
              <div>
                <label className="block text-sm text-gray-600 mb-1">SSN</label>
                <input type="text" maxLength={11} value={account.customerSSN} onChange={(e) => handleFieldChange('customerSSN', e.target.value)} className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500" />
              </div>
              <div>
                <label className="block text-sm text-gray-600 mb-1">Government ID</label>
                <input type="text" maxLength={20} value={account.governmentId} onChange={(e) => handleFieldChange('governmentId', e.target.value)} className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500" />
              </div>
              <div>
                <label className="block text-sm text-gray-600 mb-1">EFT Account ID</label>
                <input type="text" maxLength={10} value={account.eftAccountId} onChange={(e) => handleFieldChange('eftAccountId', e.target.value)} className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500" />
              </div>
            </div>
          </section>

          <div className="flex gap-3">
            <button
              onClick={handleSave}
              disabled={loading}
              className="px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-500 disabled:bg-gray-400"
            >
              {loading ? 'Saving...' : 'Save'}
            </button>
            <button
              onClick={() => navigate(-1)}
              className="px-6 py-2 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50"
            >
              Cancel
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
