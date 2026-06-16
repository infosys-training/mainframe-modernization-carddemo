import { useAuth } from '@/hooks/useAuth';

export function AppHeader() {
  const { user, logout } = useAuth();

  const now = new Date();
  const dateStr = now.toLocaleDateString('en-US', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  });
  const timeStr = now.toLocaleTimeString('en-US', {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false,
  });

  return (
    <header className="bg-blue-800 text-white px-6 py-3 flex items-center justify-between shadow-md">
      <div className="flex items-center gap-4">
        <h1 className="text-xl font-bold tracking-wide">CardDemo System</h1>
        <span className="text-blue-200 text-sm">Credit Card Management</span>
      </div>
      <div className="flex items-center gap-6 text-sm">
        <span className="text-blue-200">{dateStr}</span>
        <span className="text-blue-200">{timeStr}</span>
        {user && (
          <>
            <span className="text-yellow-300">
              {user.firstName} {user.lastName} ({user.userType === 'A' ? 'Admin' : 'User'})
            </span>
            <button
              onClick={logout}
              className="bg-blue-600 hover:bg-blue-500 px-3 py-1 rounded text-white transition-colors"
            >
              Logout
            </button>
          </>
        )}
      </div>
    </header>
  );
}
