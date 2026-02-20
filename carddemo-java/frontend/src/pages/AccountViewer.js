import React, { useState } from 'react';
import { fetchAccount } from '../services/api';

const fieldStyle = { color: '#0ff', display: 'inline-block', width: '200px' };
const valueStyle = { color: '#0f0', textDecoration: 'underline' };
const errorStyle = { color: '#f00', fontWeight: 'bold' };
const inputStyle = { backgroundColor: '#001a00', color: '#0f0', border: '1px solid #0f0', padding: '4px 8px', fontFamily: 'Courier New, monospace' };
const btnStyle = { backgroundColor: '#003300', color: '#0f0', border: '1px solid #0f0', padding: '6px 16px', cursor: 'pointer', fontFamily: 'Courier New, monospace', marginLeft: '10px' };

function AccountViewer() {
  const [acctId, setAcctId] = useState('');
  const [account, setAccount] = useState(null);
  const [customer, setCustomer] = useState(null);
  const [error, setError] = useState('');

  const handleFetch = async () => {
    setError('');
    setAccount(null);
    setCustomer(null);
    if (!acctId.trim()) {
      setError('Account Number can NOT be empty...');
      return;
    }
    try {
      const data = await fetchAccount(acctId);
      setAccount(data.account);
      setCustomer(data.customer || null);
    } catch (err) {
      setError(err.message);
    }
  };

  const handleClear = () => {
    setAcctId('');
    setAccount(null);
    setCustomer(null);
    setError('');
  };

  return (
    <div>
      <h2 style={{ color: '#fff', textAlign: 'center' }}>View Account</h2>
      <div style={{ marginBottom: '16px' }}>
        <span style={fieldStyle}>Account Number :</span>
        <input style={inputStyle} value={acctId} onChange={e => setAcctId(e.target.value)}
          onKeyDown={e => e.key === 'Enter' && handleFetch()} placeholder="Enter account ID" />
        <button style={btnStyle} onClick={handleFetch}>Fetch</button>
        <button style={btnStyle} onClick={handleClear}>Clear (F4)</button>
      </div>
      {error && <div style={errorStyle}>{error}</div>}
      {account && (
        <div>
          <div style={{ marginBottom: '4px' }}><span style={fieldStyle}>Active Y/N:</span><span style={valueStyle}>{account.activeStatus}</span></div>
          <div style={{ marginBottom: '4px' }}><span style={fieldStyle}>Opened:</span><span style={valueStyle}>{account.openDate}</span>
            <span style={{ ...fieldStyle, marginLeft: '20px' }}>Credit Limit:</span><span style={valueStyle}>{account.creditLimit}</span></div>
          <div style={{ marginBottom: '4px' }}><span style={fieldStyle}>Expiry:</span><span style={valueStyle}>{account.expirationDate}</span>
            <span style={{ ...fieldStyle, marginLeft: '20px' }}>Cash Credit Limit:</span><span style={valueStyle}>{account.cashCreditLimit}</span></div>
          <div style={{ marginBottom: '4px' }}><span style={fieldStyle}>Reissue:</span><span style={valueStyle}>{account.reissueDate}</span>
            <span style={{ ...fieldStyle, marginLeft: '20px' }}>Current Balance:</span><span style={valueStyle}>{account.currBal}</span></div>
          <div style={{ marginBottom: '4px' }}><span style={{ ...fieldStyle, marginLeft: '220px' }}>Current Cycle Credit:</span><span style={valueStyle}>{account.currCycCredit}</span></div>
          <div style={{ marginBottom: '4px' }}><span style={fieldStyle}>Account Group:</span><span style={valueStyle}>{account.groupId}</span>
            <span style={{ ...fieldStyle, marginLeft: '20px' }}>Current Cycle Debit:</span><span style={valueStyle}>{account.currCycDebit}</span></div>

          {customer && (
            <div style={{ marginTop: '16px' }}>
              <h3 style={{ color: '#fff' }}>Customer Details</h3>
              <div style={{ marginBottom: '4px' }}><span style={fieldStyle}>Customer ID:</span><span style={valueStyle}>{customer.custId}</span>
                <span style={{ ...fieldStyle, marginLeft: '20px' }}>SSN:</span><span style={valueStyle}>{customer.ssn}</span></div>
              <div style={{ marginBottom: '4px' }}><span style={fieldStyle}>Date of Birth:</span><span style={valueStyle}>{customer.dobYyyyMmDd}</span>
                <span style={{ ...fieldStyle, marginLeft: '20px' }}>FICO Score:</span><span style={valueStyle}>{customer.ficoCreditScore}</span></div>
              <div style={{ marginBottom: '4px' }}>
                <span style={valueStyle}>{customer.firstName}</span>
                <span style={{ ...valueStyle, marginLeft: '10px' }}>{customer.middleName}</span>
                <span style={{ ...valueStyle, marginLeft: '10px' }}>{customer.lastName}</span>
              </div>
              <div style={{ marginBottom: '4px' }}><span style={fieldStyle}>Address:</span><span style={valueStyle}>{customer.addrLine1}</span>
                <span style={{ ...fieldStyle, marginLeft: '20px' }}>State:</span><span style={valueStyle}>{customer.addrStateCd}</span></div>
              <div style={{ marginBottom: '4px' }}><span style={fieldStyle}></span><span style={valueStyle}>{customer.addrLine2}</span>
                <span style={{ ...fieldStyle, marginLeft: '20px' }}>Zip:</span><span style={valueStyle}>{customer.addrZip}</span></div>
              <div style={{ marginBottom: '4px' }}><span style={fieldStyle}>City:</span><span style={valueStyle}>{customer.addrLine3}</span>
                <span style={{ ...fieldStyle, marginLeft: '20px' }}>Country:</span><span style={valueStyle}>{customer.addrCountryCd}</span></div>
              <div style={{ marginBottom: '4px' }}><span style={fieldStyle}>Phone 1:</span><span style={valueStyle}>{customer.phoneNum1}</span>
                <span style={{ ...fieldStyle, marginLeft: '20px' }}>Govt ID:</span><span style={valueStyle}>{customer.govtIssuedId}</span></div>
              <div style={{ marginBottom: '4px' }}><span style={fieldStyle}>Phone 2:</span><span style={valueStyle}>{customer.phoneNum2}</span>
                <span style={{ ...fieldStyle, marginLeft: '20px' }}>EFT Account:</span><span style={valueStyle}>{customer.eftAccountId}</span>
                <span style={{ ...fieldStyle, marginLeft: '20px' }}>Primary Card Holder:</span><span style={valueStyle}>{customer.priCardHolderInd}</span></div>
            </div>
          )}
        </div>
      )}
    </div>
  );
}

export default AccountViewer;
