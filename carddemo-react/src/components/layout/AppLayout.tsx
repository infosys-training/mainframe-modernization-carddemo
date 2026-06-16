import { Outlet } from 'react-router-dom';
import { AppHeader } from './AppHeader';

export function AppLayout() {
  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      <AppHeader />
      <main className="flex-1 p-6 max-w-7xl mx-auto w-full">
        <Outlet />
      </main>
    </div>
  );
}
