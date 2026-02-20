import React, { useState, useEffect } from 'react';
import { fetchTransactions } from '../services/api';

const thStyle = { color: '#0ff', textAlign: 'left', padding: '4px 8px', borderBottom: '1px solid #0f0' };
const tdStyle = { padding: '4px 8px', color: '#0f0' };
const btnStyle = { backgroundColor: '#003300', color: '#0f0', border: '1px solid #0f0', padding: '6px 16px', cursor: 'pointer', fontFamily: 'Courier New, monospace', marginRight: '10px' };

function TransactionList() {
  const [transactions, setTransactions] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [error, setError] = useState('');

  const loadPage = async (pageNum) => {
    try {
      const data = await fetchTransactions(pageNum, 10);
      setTransactions(data.content || []);
      setTotalPages(data.totalPages || 0);
      setPage(pageNum);
      setError('');
    } catch (err) {
      setError(err.message);
    }
  };

  useEffect(() => { loadPage(0); }, []);

  return (
    <div>
      <h2 style={{ color: '#fff', textAlign: 'center' }}>Transaction List (F5=Browse)</h2>
      {error && <div style={{ color: '#f00' }}>{error}</div>}
      <table style={{ width: '100%', borderCollapse: 'collapse' }}>
        <thead>
          <tr>
            <th style={thStyle}>Tran ID</th>
            <th style={thStyle}>Type</th>
            <th style={thStyle}>Amount</th>
            <th style={thStyle}>Card Number</th>
            <th style={thStyle}>Description</th>
            <th style={thStyle}>Orig Date</th>
          </tr>
        </thead>
        <tbody>
          {transactions.map(t => (
            <tr key={t.tranId}>
              <td style={tdStyle}>{t.tranId}</td>
              <td style={tdStyle}>{t.tranTypeCd}</td>
              <td style={tdStyle}>{t.tranAmt}</td>
              <td style={tdStyle}>{t.cardNum}</td>
              <td style={tdStyle}>{t.tranDesc}</td>
              <td style={tdStyle}>{t.origTs}</td>
            </tr>
          ))}
        </tbody>
      </table>
      <div style={{ marginTop: '10px' }}>
        <button style={btnStyle} onClick={() => loadPage(page - 1)} disabled={page <= 0}>Previous</button>
        <span style={{ color: '#0ff' }}>Page {page + 1} of {totalPages}</span>
        <button style={{ ...btnStyle, marginLeft: '10px' }} onClick={() => loadPage(page + 1)} disabled={page >= totalPages - 1}>Next</button>
      </div>
    </div>
  );
}

export default TransactionList;
