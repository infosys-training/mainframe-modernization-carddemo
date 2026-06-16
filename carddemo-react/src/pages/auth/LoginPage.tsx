import { useState, type FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '@/hooks/useAuth';
import { ErrorMessage } from '@/components/common/ErrorMessage';

export function LoginPage() {
  const [userId, setUserId] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);

    if (!userId.trim() || !password.trim()) {
      setError('User ID and Password are required');
      return;
    }

    setLoading(true);
    try {
      await login(userId, password);
      const userJson = localStorage.getItem('user');
      const user = userJson ? JSON.parse(userJson) : null;
      navigate(user?.userType === 'A' ? '/admin' : '/menu');
    } catch (err: unknown) {
      const msg =
        err instanceof Error ? err.message : 'Invalid credentials. Please try again.';
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-900 to-blue-700">
      <div className="bg-white rounded-lg shadow-xl p-8 w-full max-w-md">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-blue-800">CardDemo</h1>
          <p className="text-gray-500 mt-1">Credit Card Management System</p>
        </div>

        <ErrorMessage message={error} />

        <form onSubmit={handleSubmit} className="space-y-5">
          <div>
            <label htmlFor="userId" className="block text-sm font-medium text-gray-700 mb-1">
              User ID
            </label>
            <input
              id="userId"
              type="text"
              maxLength={8}
              value={userId}
              onChange={(e) => setUserId(e.target.value)}
              className="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              placeholder="Enter User ID"
              autoFocus
            />
          </div>

          <div>
            <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-1">
              Password
            </label>
            <input
              id="password"
              type="password"
              maxLength={8}
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              placeholder="Enter Password"
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full py-2.5 bg-blue-700 text-white rounded-md font-medium hover:bg-blue-600 disabled:bg-gray-400 transition-colors"
          >
            {loading ? 'Signing in...' : 'Sign In'}
          </button>
        </form>
      </div>
    </div>
  );
}
