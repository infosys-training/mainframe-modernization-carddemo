import { useState } from "react";
import { BrowserRouter, Routes, Route, Navigate, Outlet } from "react-router-dom";
import { CssBaseline, Box, Toolbar } from "@mui/material";
import { AuthProvider, useAuth } from "./context/AuthContext";
import Header from "./components/Header";
import Sidebar, { DRAWER_WIDTH } from "./components/Sidebar";

import Login from "./pages/Login";
import MainMenu from "./pages/MainMenu";
import AdminMenu from "./pages/AdminMenu";
import AccountList from "./pages/AccountList";
import AccountForm from "./pages/AccountForm";
import AccountView from "./pages/AccountView";
import CardList from "./pages/CardList";
import CardSelect from "./pages/CardSelect";
import CardUpdate from "./pages/CardUpdate";
import TransactionList from "./pages/TransactionList";
import TransactionAdd from "./pages/TransactionAdd";
import UserList from "./pages/UserList";
import UserAdd from "./pages/UserAdd";
import UserUpdate from "./pages/UserUpdate";
import UserDelete from "./pages/UserDelete";
import Reports from "./pages/Reports";
import Billing from "./pages/Billing";

function ProtectedRoute() {
  const { isAuthenticated } = useAuth();
  const [mobileOpen, setMobileOpen] = useState(false);

  if (!isAuthenticated) return <Navigate to="/login" replace />;

  return (
    <Box sx={{ display: "flex" }}>
      <Header onMenuToggle={() => setMobileOpen((prev) => !prev)} />
      <Sidebar mobileOpen={mobileOpen} onClose={() => setMobileOpen(false)} />
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          p: 3,
          width: { md: `calc(100% - ${DRAWER_WIDTH}px)` },
        }}
      >
        <Toolbar />
        <Outlet />
      </Box>
    </Box>
  );
}

function AdminRoute() {
  const { isAdmin } = useAuth();
  if (!isAdmin) return <Navigate to="/" replace />;
  return <Outlet />;
}

function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route element={<ProtectedRoute />}>
        <Route path="/" element={<MainMenu />} />
        <Route path="/accounts" element={<AccountList />} />
        <Route path="/accounts/new" element={<AccountForm mode="new" />} />
        <Route path="/accounts/:id" element={<AccountView />} />
        <Route path="/accounts/:id/edit" element={<AccountForm mode="edit" />} />
        <Route path="/cards" element={<CardList />} />
        <Route path="/cards/:cardNum" element={<CardSelect />} />
        <Route path="/cards/:cardNum/edit" element={<CardUpdate />} />
        <Route path="/transactions" element={<TransactionList />} />
        <Route path="/transactions/add" element={<TransactionAdd />} />
        <Route path="/reports" element={<Reports />} />
        <Route path="/billing" element={<Billing />} />
        <Route element={<AdminRoute />}>
          <Route path="/admin" element={<AdminMenu />} />
          <Route path="/users" element={<UserList />} />
          <Route path="/users/add" element={<UserAdd />} />
          <Route path="/users/:userId/edit" element={<UserUpdate />} />
          <Route path="/users/:userId/delete" element={<UserDelete />} />
        </Route>
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <CssBaseline />
      <AuthProvider>
        <AppRoutes />
      </AuthProvider>
    </BrowserRouter>
  );
}
