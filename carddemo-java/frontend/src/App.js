import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import AccountViewer from './pages/AccountViewer';
import TransactionViewer from './pages/TransactionViewer';
import UserManagement from './pages/UserManagement';
import TransactionList from './pages/TransactionList';

function App() {
  return (
    <Router>
      <div style={{ fontFamily: 'Courier New, monospace', backgroundColor: '#000', color: '#0f0', minHeight: '100vh' }}>
        <header style={{ backgroundColor: '#001a00', padding: '10px 20px', borderBottom: '1px solid #0f0' }}>
          <h1 style={{ margin: 0, fontSize: '18px', color: '#ff0' }}>
            CardDemo - Credit Card Management System
          </h1>
          <nav style={{ marginTop: '8px' }}>
            <Link to="/" style={navStyle}>Account Viewer</Link>
            <Link to="/transactions" style={navStyle}>Transaction List</Link>
            <Link to="/transaction" style={navStyle}>View Transaction</Link>
            <Link to="/users" style={navStyle}>User Management</Link>
          </nav>
        </header>
        <main style={{ padding: '20px' }}>
          <Routes>
            <Route path="/" element={<AccountViewer />} />
            <Route path="/transactions" element={<TransactionList />} />
            <Route path="/transaction" element={<TransactionViewer />} />
            <Route path="/users" element={<UserManagement />} />
          </Routes>
        </main>
        <footer style={{ padding: '10px 20px', borderTop: '1px solid #0f0', fontSize: '12px', color: '#0a0' }}>
          F3=Exit | F4=Clear | F5=Browse
        </footer>
      </div>
    </Router>
  );
}

const navStyle = {
  color: '#0ff',
  marginRight: '20px',
  textDecoration: 'none',
  fontSize: '14px'
};

export default App;
