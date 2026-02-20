import React, { useState } from 'react';
import { fetchTransaction } from '../services/api';

const fieldStyle = { color: '#0ff', display: 'inline-block', width: '160px' };
const valueStyle = { color: '#0f0' };
const errorStyle = { color: '#f00', fontWeight: 'bold' };
const inputStyle = { backgroundColor: '#001a00', color: '#0f0', border: '1px solid #0f0', padding: '4px 8px', fontFamily: 'Courier New, monospace' };
const btnStyle = { backgroundColor: '#003300', color: '#0f0', border: '1px solid #0f0', padding: '6px 16px', cursor: 'pointer', fontFamily: 'Courier New, monospace', marginLeft: '10px' };

function TransactionViewer() {
  const [tranId, setTranId] = useState('');
  const [transaction, setTransaction] = useState(null);
  const [error, setError] = useState('');

  const handleFetch = async () => {
    setError('');
    setTransaction(null);
    if (!tranId.trim()) {
      setError('Tran ID can NOT be empty...');
      return;
    }
    try {
      const data = await fetchTransaction(tranId);
      setTransaction(data);
    } catch (err) {
      setError(err.message);
    }
  };

  const handleClear = () => {
    setTranId('');
    setTransaction(null);
    setError('');
  };

  return (
    <div>
      <h2 style={{ color: '#fff', textAlign: 'center' }}>View Transaction</h2>
      <div style={{ marginBottom: '16px' }}>
        <span style={fieldStyle}>Enter Tran ID:</span>
        <input style={inputStyle} value={tranId} onChange={e => setTranId(e.target.value)}
          onKeyDown={e => e.key === 'Enter' && handleFetch()} placeholder="Enter transaction ID" />
        <button style={btnStyle} onClick={handleFetch}>Fetch (Enter)</button>
        <button style={btnStyle} onClick={handleClear}>Clear (F4)</button>
      </div>
      {error && <div style={errorStyle}>{error}</div>}
      {transaction && (
        <div>
          <hr style={{ borderColor: '#0f0' }} />
          <div style={{ marginBottom: '4px' }}><span style={fieldStyle}>Transaction ID:</span><span style={valueStyle}>{transaction.tranId}</span>
            <span style={{ ...fieldStyle, marginLeft: '20px' }}>Card Number:</span><span style={valueStyle}>{transaction.cardNum}</span></div>
          <div style={{ marginBottom: '4px' }}><span style={fieldStyle}>Type CD:</span><span style={valueStyle}>{transaction.tranTypeCd}</span>
            <span style={{ ...fieldStyle, marginLeft: '20px' }}>Category CD:</span><span style={valueStyle}>{transaction.tranCatCd}</span>
            <span style={{ ...fieldStyle, marginLeft: '20px' }}>Source:</span><span style={valueStyle}>{transaction.tranSource}</span></div>
          <div style={{ marginBottom: '4px' }}><span style={fieldStyle}>Description:</span><span style={valueStyle}>{transaction.tranDesc}</span></div>
          <div style={{ marginBottom: '4px' }}><span style={fieldStyle}>Amount:</span><span style={valueStyle}>{transaction.tranAmt}</span>
            <span style={{ ...fieldStyle, marginLeft: '20px' }}>Orig Date:</span><span style={valueStyle}>{transaction.origTs}</span>
            <span style={{ ...fieldStyle, marginLeft: '20px' }}>Proc Date:</span><span style={valueStyle}>{transaction.procTs}</span></div>
          <div style={{ marginBottom: '4px' }}><span style={fieldStyle}>Merchant ID:</span><span style={valueStyle}>{transaction.merchantId}</span>
            <span style={{ ...fieldStyle, marginLeft: '20px' }}>Merchant Name:</span><span style={valueStyle}>{transaction.merchantName}</span></div>
          <div style={{ marginBottom: '4px' }}><span style={fieldStyle}>Merchant City:</span><span style={valueStyle}>{transaction.merchantCity}</span>
            <span style={{ ...fieldStyle, marginLeft: '20px' }}>Merchant Zip:</span><span style={valueStyle}>{transaction.merchantZip}</span></div>
        </div>
      )}
    </div>
  );
}

export default TransactionViewer;
