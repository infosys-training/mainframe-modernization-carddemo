const API_BASE = '/api';

export async function fetchAccount(acctId) {
  const response = await fetch(`${API_BASE}/accounts/${acctId}`);
  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message || 'Failed to fetch account');
  }
  return response.json();
}

export async function fetchTransaction(tranId) {
  const response = await fetch(`${API_BASE}/transactions/${tranId}`);
  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message || 'Failed to fetch transaction');
  }
  return response.json();
}

export async function fetchTransactions(page = 0, size = 10) {
  const response = await fetch(`${API_BASE}/transactions?page=${page}&size=${size}`);
  if (!response.ok) throw new Error('Failed to fetch transactions');
  return response.json();
}

export async function fetchUsers() {
  const response = await fetch(`${API_BASE}/users`);
  if (!response.ok) throw new Error('Failed to fetch users');
  return response.json();
}

export async function addUser(user) {
  const response = await fetch(`${API_BASE}/users`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(user)
  });
  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message || 'Failed to add user');
  }
  return response.json();
}

export async function deleteUser(usrId) {
  const response = await fetch(`${API_BASE}/users/${usrId}`, { method: 'DELETE' });
  if (!response.ok) throw new Error('Failed to delete user');
}

export async function runBatchJob(jobType, params = {}) {
  const queryString = new URLSearchParams(params).toString();
  const url = `${API_BASE}/batch/${jobType}${queryString ? '?' + queryString : ''}`;
  const response = await fetch(url, { method: 'POST' });
  if (!response.ok) throw new Error('Failed to run batch job');
  return response.json();
}
