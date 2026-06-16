import { useState, type FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { userApi } from '@/api';
import { ErrorMessage } from '@/components/common/ErrorMessage';

export function UserAddPage() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    userId: '',
    firstName: '',
    lastName: '',
    password: '',
    userType: 'U' as 'A' | 'U',
  });
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleChange = (field: string, value: string) => {
    setForm((prev) => ({ ...prev, [field]: value }));
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);

    if (!form.userId || !form.firstName || !form.lastName || !form.password) {
      setError('All fields are required');
      return;
    }

    setLoading(true);
    try {
      await userApi.create(form);
      setSuccess('User created successfully');
      setForm({ userId: '', firstName: '', lastName: '', password: '', userType: 'U' });
    } catch {
      setError('Failed to create user');
    } finally {
      setLoading(false);
    }
  };

  const handleClear = () => {
    setForm({ userId: '', firstName: '', lastName: '', password: '', userType: 'U' });
    setError(null);
    setSuccess(null);
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-2xl font-bold text-gray-800">Add User</h2>
        <button onClick={() => navigate('/admin')} className="text-blue-600 hover:text-blue-800">
          &larr; Back
        </button>
      </div>

      <ErrorMessage message={error} />
      <ErrorMessage message={success} type="success" />

      <form onSubmit={handleSubmit} className="bg-white rounded-lg shadow-sm border p-6 max-w-lg space-y-4">
        <div>
          <label className="block text-sm text-gray-600 mb-1">User ID (8 chars)</label>
          <input type="text" maxLength={8} value={form.userId} onChange={(e) => handleChange('userId', e.target.value)} className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500" />
        </div>
        <div>
          <label className="block text-sm text-gray-600 mb-1">First Name (20 chars)</label>
          <input type="text" maxLength={20} value={form.firstName} onChange={(e) => handleChange('firstName', e.target.value)} className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500" />
        </div>
        <div>
          <label className="block text-sm text-gray-600 mb-1">Last Name (20 chars)</label>
          <input type="text" maxLength={20} value={form.lastName} onChange={(e) => handleChange('lastName', e.target.value)} className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500" />
        </div>
        <div>
          <label className="block text-sm text-gray-600 mb-1">Password (8 chars)</label>
          <input type="password" maxLength={8} value={form.password} onChange={(e) => handleChange('password', e.target.value)} className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500" />
        </div>
        <div>
          <label className="block text-sm text-gray-600 mb-1">User Type</label>
          <select value={form.userType} onChange={(e) => handleChange('userType', e.target.value)} className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500">
            <option value="U">User</option>
            <option value="A">Admin</option>
          </select>
        </div>
        <div className="flex gap-3 pt-4">
          <button type="submit" disabled={loading} className="px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-500 disabled:bg-gray-400">
            {loading ? 'Creating...' : 'Create User'}
          </button>
          <button type="button" onClick={handleClear} className="px-6 py-2 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50">
            Clear
          </button>
        </div>
      </form>
    </div>
  );
}
