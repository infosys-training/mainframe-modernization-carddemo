-- CardDemo Sample Data - migrated from COBOL VSAM EBCDIC data files
-- Original data files: app/data/EBCDIC/ (AWS-M2-CardDemo-*)

-- Users (from USRSEC VSAM file)
-- Original COBOL: SEC-USR-ID, SEC-USR-FNAME, SEC-USR-LNAME, SEC-USR-PWD, SEC-USR-TYPE
INSERT INTO card_demo_users (user_id, first_name, last_name, password, user_type) VALUES ('ADMIN01', 'ADMIN', 'USER', 'ADMIN01', 'A');
INSERT INTO card_demo_users (user_id, first_name, last_name, password, user_type) VALUES ('USER0001', 'FIRST01', 'LAST01', 'USER0001', 'U');
INSERT INTO card_demo_users (user_id, first_name, last_name, password, user_type) VALUES ('USER0002', 'FIRST02', 'LAST02', 'USER0002', 'U');
INSERT INTO card_demo_users (user_id, first_name, last_name, password, user_type) VALUES ('USER0003', 'FIRST03', 'LAST03', 'USER0003', 'U');

-- Customers (from CUSTDAT VSAM file)
INSERT INTO customers (cust_id, first_name, middle_name, last_name, addr_line_1, addr_line_2, addr_line_3, state_cd, country_cd, addr_zip, phone_num_1, phone_num_2, ssn, govt_issued_id, dob, eft_account_id, pri_card_holder_ind, fico_credit_score)
VALUES ('000000001', 'John', 'Michael', 'Smith', '123 Main Street', 'Apt 4B', '', 'NY', 'US', '10001', '212-555-0101', '212-555-0102', '123456789', 'DL12345678', '1985-03-15', '0000000001', 'Y', 750);

INSERT INTO customers (cust_id, first_name, middle_name, last_name, addr_line_1, addr_line_2, addr_line_3, state_cd, country_cd, addr_zip, phone_num_1, phone_num_2, ssn, govt_issued_id, dob, eft_account_id, pri_card_holder_ind, fico_credit_score)
VALUES ('000000002', 'Jane', 'Elizabeth', 'Doe', '456 Oak Avenue', '', '', 'CA', 'US', '90210', '310-555-0201', '', '987654321', 'DL87654321', '1990-07-22', '0000000002', 'Y', 680);

INSERT INTO customers (cust_id, first_name, middle_name, last_name, addr_line_1, addr_line_2, addr_line_3, state_cd, country_cd, addr_zip, phone_num_1, phone_num_2, ssn, govt_issued_id, dob, eft_account_id, pri_card_holder_ind, fico_credit_score)
VALUES ('000000003', 'Robert', 'James', 'Johnson', '789 Pine Road', 'Suite 100', '', 'TX', 'US', '75001', '214-555-0301', '214-555-0302', '456789123', 'DL45678912', '1978-11-30', '0000000003', 'Y', 720);

-- Accounts (from ACCTDAT VSAM file)
INSERT INTO accounts (acct_id, active_status, curr_bal, credit_limit, cash_credit_limit, open_date, expiration_date, reissue_date, curr_cyc_credit, curr_cyc_debit, addr_zip, group_id)
VALUES ('00000000001', 'Y', 1500.00, 5000.00, 1000.00, '2020-01-15', '2025-01-15', '2023-01-15', 200.00, 1700.00, '10001', 'GROUP001');

INSERT INTO accounts (acct_id, active_status, curr_bal, credit_limit, cash_credit_limit, open_date, expiration_date, reissue_date, curr_cyc_credit, curr_cyc_debit, addr_zip, group_id)
VALUES ('00000000002', 'Y', 3200.50, 10000.00, 2000.00, '2019-06-01', '2024-06-01', '2022-06-01', 500.00, 3700.50, '90210', 'GROUP001');

INSERT INTO accounts (acct_id, active_status, curr_bal, credit_limit, cash_credit_limit, open_date, expiration_date, reissue_date, curr_cyc_credit, curr_cyc_debit, addr_zip, group_id)
VALUES ('00000000003', 'Y', 750.25, 3000.00, 500.00, '2021-03-10', '2026-03-10', '2024-03-10', 100.00, 850.25, '75001', 'GROUP002');

-- Cards (from CARDDAT VSAM file)
INSERT INTO cards (card_num, acct_id, cvv_cd, embossed_name, expiration_date, active_status)
VALUES ('4111111111111111', '00000000001', '123', 'JOHN M SMITH', '2025-01-15', 'Y');

INSERT INTO cards (card_num, acct_id, cvv_cd, embossed_name, expiration_date, active_status)
VALUES ('4222222222222222', '00000000002', '456', 'JANE E DOE', '2024-06-01', 'Y');

INSERT INTO cards (card_num, acct_id, cvv_cd, embossed_name, expiration_date, active_status)
VALUES ('4333333333333333', '00000000003', '789', 'ROBERT J JOHNSON', '2026-03-10', 'Y');

INSERT INTO cards (card_num, acct_id, cvv_cd, embossed_name, expiration_date, active_status)
VALUES ('4444444444444444', '00000000001', '321', 'JOHN M SMITH', '2025-01-15', 'N');

-- Card Cross-References (from CCXREF / CXACAIX VSAM files)
INSERT INTO card_xref (card_num, cust_id, acct_id) VALUES ('4111111111111111', '000000001', '00000000001');
INSERT INTO card_xref (card_num, cust_id, acct_id) VALUES ('4222222222222222', '000000002', '00000000002');
INSERT INTO card_xref (card_num, cust_id, acct_id) VALUES ('4333333333333333', '000000003', '00000000003');
INSERT INTO card_xref (card_num, cust_id, acct_id) VALUES ('4444444444444444', '000000001', '00000000001');

-- Transactions (from TRANSACT VSAM file)
INSERT INTO transactions (tran_id, type_cd, cat_cd, source, description, amount, merchant_id, merchant_name, merchant_city, merchant_zip, card_num, orig_ts, proc_ts)
VALUES ('0000000000000001', '01', 5001, 'POS TERM', 'GROCERY STORE PURCHASE', 125.50, 100000001, 'MEGA MART', 'NEW YORK', '10001', '4111111111111111', '2024-01-15 10:30:00.000000', '2024-01-15 10:30:05.000000');

INSERT INTO transactions (tran_id, type_cd, cat_cd, source, description, amount, merchant_id, merchant_name, merchant_city, merchant_zip, card_num, orig_ts, proc_ts)
VALUES ('0000000000000002', '01', 5002, 'ONLINE', 'ONLINE ELECTRONICS PURCHASE', 599.99, 100000002, 'TECH WORLD', 'SAN JOSE', '95101', '4111111111111111', '2024-01-16 14:22:00.000000', '2024-01-16 14:22:03.000000');

INSERT INTO transactions (tran_id, type_cd, cat_cd, source, description, amount, merchant_id, merchant_name, merchant_city, merchant_zip, card_num, orig_ts, proc_ts)
VALUES ('0000000000000003', '01', 5003, 'POS TERM', 'RESTAURANT DINING', 85.00, 100000003, 'FINE DINING REST', 'LOS ANGELES', '90001', '4222222222222222', '2024-01-17 19:45:00.000000', '2024-01-17 19:45:02.000000');

INSERT INTO transactions (tran_id, type_cd, cat_cd, source, description, amount, merchant_id, merchant_name, merchant_city, merchant_zip, card_num, orig_ts, proc_ts)
VALUES ('0000000000000004', '01', 5004, 'POS TERM', 'GAS STATION FUEL', 55.75, 100000004, 'QUICKFUEL STATION', 'DALLAS', '75001', '4333333333333333', '2024-01-18 08:15:00.000000', '2024-01-18 08:15:01.000000');

INSERT INTO transactions (tran_id, type_cd, cat_cd, source, description, amount, merchant_id, merchant_name, merchant_city, merchant_zip, card_num, orig_ts, proc_ts)
VALUES ('0000000000000005', '02', 2, 'POS TERM', 'BILL PAYMENT - ONLINE', 500.00, 999999999, 'BILL PAYMENT', 'N/A', 'N/A', '4222222222222222', '2024-01-19 12:00:00.000000', '2024-01-19 12:00:01.000000');

INSERT INTO transactions (tran_id, type_cd, cat_cd, source, description, amount, merchant_id, merchant_name, merchant_city, merchant_zip, card_num, orig_ts, proc_ts)
VALUES ('0000000000000006', '01', 5001, 'ONLINE', 'CLOTHING PURCHASE', 210.00, 100000005, 'FASHION HUB', 'CHICAGO', '60601', '4222222222222222', '2024-01-20 16:30:00.000000', '2024-01-20 16:30:04.000000');

INSERT INTO transactions (tran_id, type_cd, cat_cd, source, description, amount, merchant_id, merchant_name, merchant_city, merchant_zip, card_num, orig_ts, proc_ts)
VALUES ('0000000000000007', '01', 5005, 'POS TERM', 'PHARMACY PURCHASE', 45.25, 100000006, 'HEALTH PHARMACY', 'NEW YORK', '10002', '4111111111111111', '2024-01-21 09:00:00.000000', '2024-01-21 09:00:02.000000');
