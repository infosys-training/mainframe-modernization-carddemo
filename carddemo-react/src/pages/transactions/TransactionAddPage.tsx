import { useState, type FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { transactionApi } from '@/api';
import { ErrorMessage } from '@/components/common/ErrorMessage';
import { ConfirmDialog } from '@/components/common/ConfirmDialog';

interface TransactionForm {
  accountId: string;
  cardNumber: string;
  typeCode: string;
  categoryCode: string;
  source: string;
  description: string;
  amount: string;
  originalDate: string;
  processedDate: string;
  merchantId: string;
  merchantName: string;
  merchantCity: string;
  merchantZip: string;
}

const emptyForm: TransactionForm = {
  accountId: '',
  cardNumber: '',
  typeCode: '',
  categoryCode: '',
  source: '',
  description: '',
  amount: '',
  originalDate: '',
  processedDate: '',
  merchantId: '',
  merchantName: '',
  merchantCity: '',
  merchantZip: '',
};

export function TransactionAddPage() {
  const navigate = useNavigate();
  const [form, setForm] = useState<TransactionForm>(emptyForm);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);

  const handleChange = (field: keyof TransactionForm, value: string) => {
    setForm((prev) => ({ ...prev, [field]: value }));
  };

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    setError(null);

    if (!form.accountId && !form.cardNumber) {
      setError('Account ID or Card Number is required');
      return;
    }
    if (!form.typeCode || !form.amount) {
      setError('Type Code and Amount are required');
      return;
    }

    setShowConfirm(true);
  };

  const handleConfirm = async () => {
    setShowConfirm(false);
    setLoading(true);
    setError(null);
    try {
      await transactionApi.create({
        accountId: Number(form.accountId) || 0,
        cardNumber: form.cardNumber,
        typeCode: form.typeCode,
        categoryCode: form.categoryCode,
        source: form.source,
        description: form.description,
        amount: Number(form.amount),
        originalDate: form.originalDate,
        processedDate: form.processedDate,
        merchantId: form.merchantId,
        merchantName: form.merchantName,
        merchantCity: form.merchantCity,
        merchantZip: form.merchantZip,
      });
      setSuccess('Transaction created successfully');
      setForm(emptyForm);
    } catch {
      setError('Failed to create transaction');
    } finally {
      setLoading(false);
    }
  };

  const handleClear = () => {
    setForm(emptyForm);
    setError(null);
    setSuccess(null);
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-2xl font-bold text-gray-800">Add Transaction</h2>
        <button onClick={() => navigate(-1)} className="text-blue-600 hover:text-blue-800">
          &larr; Back
        </button>
      </div>

      <ErrorMessage message={error} />
      <ErrorMessage message={success} type="success" />

      <form onSubmit={handleSubmit} className="bg-white rounded-lg shadow-sm border p-6 space-y-6">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          <div>
            <label className="block text-sm text-gray-600 mb-1">Account # (11 chars)</label>
            <input type="text" maxLength={11} value={form.accountId} onChange={(e) => handleChange('accountId', e.target.value.replace(/\D/g, ''))} className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500" />
          </div>
          <div>
            <label className="block text-sm text-gray-600 mb-1">Card # (16 chars)</label>
            <input type="text" maxLength={16} value={form.cardNumber} onChange={(e) => handleChange('cardNumber', e.target.value)} className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500" />
          </div>
          <div>
            <label className="block text-sm text-gray-600 mb-1">Type Code (2 chars)</label>
            <input type="text" maxLength={2} value={form.typeCode} onChange={(e) => handleChange('typeCode', e.target.value)} className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500" />
          </div>
          <div>
            <label className="block text-sm text-gray-600 mb-1">Category Code (4 chars)</label>
            <input type="text" maxLength={4} value={form.categoryCode} onChange={(e) => handleChange('categoryCode', e.target.value)} className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500" />
          </div>
          <div>
            <label className="block text-sm text-gray-600 mb-1">Source (10 chars)</label>
            <input type="text" maxLength={10} value={form.source} onChange={(e) => handleChange('source', e.target.value)} className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500" />
          </div>
          <div>
            <label className="block text-sm text-gray-600 mb-1">Amount</label>
            <input type="number" step="0.01" min="-99999999.99" max="99999999.99" value={form.amount} onChange={(e) => handleChange('amount', e.target.value)} className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500" />
          </div>
          <div className="md:col-span-2 lg:col-span-3">
            <label className="block text-sm text-gray-600 mb-1">Description (60 chars)</label>
            <input type="text" maxLength={60} value={form.description} onChange={(e) => handleChange('description', e.target.value)} className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500" />
          </div>
          <div>
            <label className="block text-sm text-gray-600 mb-1">Original Date (YYYY-MM-DD)</label>
            <input type="date" value={form.originalDate} onChange={(e) => handleChange('originalDate', e.target.value)} className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500" />
          </div>
          <div>
            <label className="block text-sm text-gray-600 mb-1">Processed Date (YYYY-MM-DD)</label>
            <input type="date" value={form.processedDate} onChange={(e) => handleChange('processedDate', e.target.value)} className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500" />
          </div>
        </div>

        <div>
          <h3 className="text-md font-semibold text-gray-700 mb-3">Merchant Information</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            <div>
              <label className="block text-sm text-gray-600 mb-1">Merchant ID (9 chars)</label>
              <input type="text" maxLength={9} value={form.merchantId} onChange={(e) => handleChange('merchantId', e.target.value)} className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500" />
            </div>
            <div>
              <label className="block text-sm text-gray-600 mb-1">Merchant Name (30 chars)</label>
              <input type="text" maxLength={30} value={form.merchantName} onChange={(e) => handleChange('merchantName', e.target.value)} className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500" />
            </div>
            <div>
              <label className="block text-sm text-gray-600 mb-1">Merchant City (25 chars)</label>
              <input type="text" maxLength={25} value={form.merchantCity} onChange={(e) => handleChange('merchantCity', e.target.value)} className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500" />
            </div>
            <div>
              <label className="block text-sm text-gray-600 mb-1">Merchant Zip (10 chars)</label>
              <input type="text" maxLength={10} value={form.merchantZip} onChange={(e) => handleChange('merchantZip', e.target.value)} className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500" />
            </div>
          </div>
        </div>

        <div className="flex gap-3 pt-4">
          <button type="submit" disabled={loading} className="px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-500 disabled:bg-gray-400">
            {loading ? 'Submitting...' : 'Submit'}
          </button>
          <button type="button" onClick={handleClear} className="px-6 py-2 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50">
            Clear
          </button>
        </div>
      </form>

      <ConfirmDialog
        open={showConfirm}
        title="Confirm Transaction"
        message="Are you sure you want to submit this transaction?"
        onConfirm={handleConfirm}
        onCancel={() => setShowConfirm(false)}
      />
    </div>
  );
}
