import { useState, useEffect, type FormEvent } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { userApi } from '@/api';
import { ErrorMessage } from '@/components/common/ErrorMessage';

export function UserEditPage() {
  const { id } = useParams<{ id: string }>();
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

  useEffect(() => {
    if (id) {
      userApi
        .list({ page: 1, size: 100 })
        .then((result) => {
          const user = result.data.find((u) => u.userId === id);
          if (user) {
            setForm({
              userId: user.userId,
              firstName: user.firstName,
              lastName: user.lastName,
              password: '',
              userType: user.userType,
            });
          } else {
            setError('User not found');
          }
        })
        .catch(() => setError('Failed to load user'));
    }
  }, [id]);

  const handleChange = (field: string, value: string) => {
    setForm((prev) => ({ ...prev, [field]: value }));
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (!id) return;
    setError(null);
    setSuccess(null);
    setLoading(true);
    try {
      await userApi.update(id, {
        ...form,
        password: form.password || undefined,
      });
      setSuccess('User updated successfully');
    } catch {
      setError('Failed to update user');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-2xl font-bold text-gray-800">Edit User</h2>
        <button onClick={() => navigate('/admin/users')} className="text-blue-600 hover:text-blue-800">
          &larr; Back
        </button>
      </div>

      <ErrorMessage message={error} />
      <ErrorMessage message={success} type="success" />

      <form onSubmit={handleSubmit} className="bg-white rounded-lg shadow-sm border p-6 max-w-lg space-y-4">
        <div>
          <label className="block text-sm text-gray-600 mb-1">User ID</label>
          <input type="text" value={form.userId} disabled className="w-full px-3 py-2 bg-gray-100 border rounded-md" />
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
          <label className="block text-sm text-gray-600 mb-1">Password (leave blank to keep unchanged)</label>
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
            {loading ? 'Saving...' : 'Save Changes'}
          </button>
          <button type="button" onClick={() => navigate(`/admin/users/${id}/delete`)} className="px-6 py-2 bg-red-600 text-white rounded-md hover:bg-red-500">
            Delete
          </button>
          <button type="button" onClick={() => navigate('/admin/users')} className="px-6 py-2 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50">
            Cancel
          </button>
        </div>
      </form>
    </div>
  );
}
