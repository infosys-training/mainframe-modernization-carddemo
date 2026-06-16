import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { userApi } from '@/api';
import { ErrorMessage } from '@/components/common/ErrorMessage';
import { ConfirmDialog } from '@/components/common/ConfirmDialog';

export function UserDeletePage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [user, setUser] = useState<{ userId: string; firstName: string; lastName: string; userType: string } | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [showConfirm, setShowConfirm] = useState(true);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (id) {
      userApi
        .list({ page: 1, size: 100 })
        .then((result) => {
          const found = result.data.find((u) => u.userId === id);
          if (found) {
            setUser(found);
          } else {
            setError('User not found');
          }
        })
        .catch(() => setError('Failed to load user'));
    }
  }, [id]);

  const handleDelete = async () => {
    if (!id) return;
    setShowConfirm(false);
    setLoading(true);
    try {
      await userApi.delete(id);
      navigate('/admin/users');
    } catch {
      setError('Failed to delete user');
      setLoading(false);
    }
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-2xl font-bold text-gray-800">Delete User</h2>
        <button onClick={() => navigate('/admin/users')} className="text-blue-600 hover:text-blue-800">
          &larr; Back
        </button>
      </div>

      <ErrorMessage message={error} />

      {user && (
        <section className="bg-white rounded-lg shadow-sm border p-6 max-w-lg">
          <h3 className="text-lg font-semibold text-red-700 mb-4">Confirm Deletion</h3>
          <dl className="grid grid-cols-2 gap-x-4 gap-y-3 text-sm mb-6">
            <dt className="text-gray-500">User ID</dt>
            <dd className="font-medium">{user.userId}</dd>
            <dt className="text-gray-500">First Name</dt>
            <dd className="font-medium">{user.firstName}</dd>
            <dt className="text-gray-500">Last Name</dt>
            <dd className="font-medium">{user.lastName}</dd>
            <dt className="text-gray-500">User Type</dt>
            <dd className="font-medium">{user.userType === 'A' ? 'Admin' : 'User'}</dd>
          </dl>
          <div className="flex gap-3">
            <button
              onClick={() => setShowConfirm(true)}
              disabled={loading}
              className="px-6 py-2 bg-red-600 text-white rounded-md hover:bg-red-500 disabled:bg-gray-400"
            >
              {loading ? 'Deleting...' : 'Delete User'}
            </button>
            <button onClick={() => navigate('/admin/users')} className="px-6 py-2 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50">
              Cancel
            </button>
          </div>
        </section>
      )}

      <ConfirmDialog
        open={showConfirm && !!user}
        title="Delete User"
        message={`Are you sure you want to delete user "${user?.userId}"? This action cannot be undone.`}
        onConfirm={handleDelete}
        onCancel={() => {
          setShowConfirm(false);
          navigate('/admin/users');
        }}
      />
    </div>
  );
}
