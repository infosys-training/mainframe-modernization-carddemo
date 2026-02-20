import React, { useState, useEffect } from 'react';
import { fetchUsers, addUser, deleteUser } from '../services/api';

const fieldStyle = { color: '#0ff', display: 'inline-block', width: '130px' };
const inputStyle = { backgroundColor: '#001a00', color: '#0f0', border: '1px solid #0f0', padding: '4px 8px', fontFamily: 'Courier New, monospace', marginBottom: '6px' };
const btnStyle = { backgroundColor: '#003300', color: '#0f0', border: '1px solid #0f0', padding: '6px 16px', cursor: 'pointer', fontFamily: 'Courier New, monospace', marginRight: '10px' };
const thStyle = { color: '#0ff', textAlign: 'left', padding: '4px 8px', borderBottom: '1px solid #0f0' };
const tdStyle = { padding: '4px 8px', color: '#0f0' };

function UserManagement() {
  const [users, setUsers] = useState([]);
  const [form, setForm] = useState({ usrId: '', usrFname: '', usrLname: '', usrPwd: '', usrType: '' });
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const loadUsers = async () => {
    try {
      const data = await fetchUsers();
      setUsers(data);
    } catch (err) {
      setError(err.message);
    }
  };

  useEffect(() => { loadUsers(); }, []);

  const handleSubmit = async () => {
    setError('');
    setMessage('');
    try {
      const result = await addUser(form);
      setMessage(result.message);
      setForm({ usrId: '', usrFname: '', usrLname: '', usrPwd: '', usrType: '' });
      loadUsers();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleDelete = async (usrId) => {
    try {
      await deleteUser(usrId);
      setMessage('User ' + usrId + ' deleted');
      loadUsers();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleClear = () => {
    setForm({ usrId: '', usrFname: '', usrLname: '', usrPwd: '', usrType: '' });
    setMessage('');
    setError('');
  };

  const updateField = (field, value) => setForm(prev => ({ ...prev, [field]: value }));

  return (
    <div>
      <h2 style={{ color: '#fff', textAlign: 'center' }}>Add User (COUSR01C)</h2>
      {message && <div style={{ color: '#0f0', marginBottom: '8px' }}>{message}</div>}
      {error && <div style={{ color: '#f00', marginBottom: '8px' }}>{error}</div>}
      <div>
        <div><span style={fieldStyle}>First Name:</span><input style={inputStyle} value={form.usrFname} onChange={e => updateField('usrFname', e.target.value)} /></div>
        <div><span style={fieldStyle}>Last Name:</span><input style={inputStyle} value={form.usrLname} onChange={e => updateField('usrLname', e.target.value)} /></div>
        <div><span style={fieldStyle}>User ID:</span><input style={inputStyle} value={form.usrId} onChange={e => updateField('usrId', e.target.value)} maxLength={8} /></div>
        <div><span style={fieldStyle}>Password:</span><input style={inputStyle} type="password" value={form.usrPwd} onChange={e => updateField('usrPwd', e.target.value)} maxLength={8} /></div>
        <div><span style={fieldStyle}>User Type:</span><input style={inputStyle} value={form.usrType} onChange={e => updateField('usrType', e.target.value)} maxLength={1} placeholder="A/R" /></div>
        <div style={{ marginTop: '10px' }}>
          <button style={btnStyle} onClick={handleSubmit}>Add User (Enter)</button>
          <button style={btnStyle} onClick={handleClear}>Clear (F4)</button>
        </div>
      </div>
      <h3 style={{ color: '#fff', marginTop: '20px' }}>Existing Users</h3>
      <table style={{ width: '100%', borderCollapse: 'collapse' }}>
        <thead>
          <tr>
            <th style={thStyle}>User ID</th>
            <th style={thStyle}>First Name</th>
            <th style={thStyle}>Last Name</th>
            <th style={thStyle}>Type</th>
            <th style={thStyle}>Actions</th>
          </tr>
        </thead>
        <tbody>
          {users.map(u => (
            <tr key={u.usrId}>
              <td style={tdStyle}>{u.usrId}</td>
              <td style={tdStyle}>{u.usrFname}</td>
              <td style={tdStyle}>{u.usrLname}</td>
              <td style={tdStyle}>{u.usrType === 'A' ? 'Admin' : 'Regular'}</td>
              <td style={tdStyle}><button style={{ ...btnStyle, fontSize: '11px', padding: '2px 8px' }} onClick={() => handleDelete(u.usrId)}>Delete</button></td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default UserManagement;
