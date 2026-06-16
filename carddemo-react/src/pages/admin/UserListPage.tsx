import { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { userApi } from '@/api';
import { DataTable } from '@/components/common/DataTable';
import { Pagination } from '@/components/common/Pagination';
import { ErrorMessage } from '@/components/common/ErrorMessage';
import type { SecUser } from '@/types';

export function UserListPage() {
  const navigate = useNavigate();
  const [users, setUsers] = useState<SecUser[]>([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(0);
  const [error, setError] = useState<string | null>(null);

  const fetchUsers = useCallback(async (page: number) => {
    try {
      const result = await userApi.list({ page });
      setUsers(result.data);
      setTotalPages(result.totalPages);
      setCurrentPage(result.currentPage);
    } catch {
      setError('Failed to fetch users');
    }
  }, []);

  useEffect(() => {
    const controller = new AbortController();
    // eslint-disable-next-line react-hooks/set-state-in-effect -- initial data fetch on mount
    fetchUsers(1);
    return () => controller.abort();
  }, [fetchUsers]);

  const columns = [
    { key: 'userId', header: 'User ID' },
    { key: 'firstName', header: 'First Name' },
    { key: 'lastName', header: 'Last Name' },
    {
      key: 'userType',
      header: 'Type',
      render: (user: SecUser) => (
        <span className={user.userType === 'A' ? 'text-purple-600 font-medium' : ''}>
          {user.userType === 'A' ? 'Admin' : 'User'}
        </span>
      ),
    },
  ];

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-2xl font-bold text-gray-800">User Management</h2>
        <div className="flex gap-3">
          <button onClick={() => navigate('/admin/users/add')} className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-500">
            Add User
          </button>
          <button onClick={() => navigate('/admin')} className="text-blue-600 hover:text-blue-800">
            &larr; Back
          </button>
        </div>
      </div>

      <ErrorMessage message={error} />

      <DataTable
        columns={columns}
        data={users}
        keyExtractor={(user) => user.userId}
        onRowClick={(user) => navigate(`/admin/users/${user.userId}/edit`)}
      />

      <Pagination currentPage={currentPage} totalPages={totalPages} onPageChange={fetchUsers} />
    </div>
  );
}
