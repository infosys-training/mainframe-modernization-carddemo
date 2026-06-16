import { createBrowserRouter, Navigate } from 'react-router-dom';
import { AppLayout } from '@/components/layout/AppLayout';
import { ProtectedRoute } from '@/components/ProtectedRoute';
import { LoginPage } from '@/pages/auth/LoginPage';
import { MainMenuPage } from '@/pages/menu/MainMenuPage';
import { AdminMenuPage } from '@/pages/menu/AdminMenuPage';
import { AccountViewPage } from '@/pages/accounts/AccountViewPage';
import { AccountEditPage } from '@/pages/accounts/AccountEditPage';
import { CardListPage } from '@/pages/cards/CardListPage';
import { CardViewPage } from '@/pages/cards/CardViewPage';
import { CardEditPage } from '@/pages/cards/CardEditPage';
import { TransactionListPage } from '@/pages/transactions/TransactionListPage';
import { TransactionViewPage } from '@/pages/transactions/TransactionViewPage';
import { TransactionAddPage } from '@/pages/transactions/TransactionAddPage';
import { ReportsPage } from '@/pages/reports/ReportsPage';
import { BillPaymentPage } from '@/pages/billing/BillPaymentPage';
import { UserListPage } from '@/pages/admin/UserListPage';
import { UserAddPage } from '@/pages/admin/UserAddPage';
import { UserEditPage } from '@/pages/admin/UserEditPage';
import { UserDeletePage } from '@/pages/admin/UserDeletePage';

export const router = createBrowserRouter([
  { path: '/login', element: <LoginPage /> },
  {
    path: '/',
    element: (
      <ProtectedRoute>
        <AppLayout />
      </ProtectedRoute>
    ),
    children: [
      { index: true, element: <Navigate to="/menu" replace /> },
      { path: 'menu', element: <MainMenuPage /> },
      {
        path: 'admin',
        element: (
          <ProtectedRoute requiredRole="A">
            <AdminMenuPage />
          </ProtectedRoute>
        ),
      },
      { path: 'accounts/:id', element: <AccountViewPage /> },
      { path: 'accounts/:id/edit', element: <AccountEditPage /> },
      { path: 'cards', element: <CardListPage /> },
      { path: 'cards/:num', element: <CardViewPage /> },
      { path: 'cards/:num/edit', element: <CardEditPage /> },
      { path: 'transactions', element: <TransactionListPage /> },
      { path: 'transactions/:id', element: <TransactionViewPage /> },
      { path: 'transactions/add', element: <TransactionAddPage /> },
      { path: 'reports', element: <ReportsPage /> },
      { path: 'billing', element: <BillPaymentPage /> },
      {
        path: 'admin/users',
        element: (
          <ProtectedRoute requiredRole="A">
            <UserListPage />
          </ProtectedRoute>
        ),
      },
      {
        path: 'admin/users/add',
        element: (
          <ProtectedRoute requiredRole="A">
            <UserAddPage />
          </ProtectedRoute>
        ),
      },
      {
        path: 'admin/users/:id/edit',
        element: (
          <ProtectedRoute requiredRole="A">
            <UserEditPage />
          </ProtectedRoute>
        ),
      },
      {
        path: 'admin/users/:id/delete',
        element: (
          <ProtectedRoute requiredRole="A">
            <UserDeletePage />
          </ProtectedRoute>
        ),
      },
    ],
  },
]);
