INSERT INTO customers (cust_id, first_name, middle_name, last_name, addr_line_1, addr_line_2, addr_line_3, addr_state_cd, addr_country_cd, addr_zip, phone_num_1, phone_num_2, ssn, govt_issued_id, dob_yyyy_mm_dd, eft_account_id, pri_card_holder_ind, fico_credit_score)
VALUES (1, 'John', 'M', 'Smith', '123 Main St', 'Apt 4B', '', 'NY', 'US', '10001', '212-555-0101', '212-555-0102', 0, 'SAMPLE-ID-1', '1985-03-15', 'EFT001', 'Y', 750);

INSERT INTO customers (cust_id, first_name, middle_name, last_name, addr_line_1, addr_line_2, addr_line_3, addr_state_cd, addr_country_cd, addr_zip, phone_num_1, phone_num_2, ssn, govt_issued_id, dob_yyyy_mm_dd, eft_account_id, pri_card_holder_ind, fico_credit_score)
VALUES (2, 'Jane', 'A', 'Doe', '456 Oak Ave', '', '', 'CA', 'US', '90210', '310-555-0201', '', 0, 'SAMPLE-ID-2', '1990-07-22', 'EFT002', 'Y', 680);

INSERT INTO accounts (acct_id, active_status, curr_bal, credit_limit, cash_credit_limit, open_date, expiration_date, reissue_date, curr_cyc_credit, curr_cyc_debit, addr_zip, group_id)
VALUES (10000000001, 'Y', 5000.00, 15000.00, 5000.00, '2020-01-15', '2025-01-15', '2023-01-15', 500.00, 1200.00, '10001', 'PREMIUM');

INSERT INTO accounts (acct_id, active_status, curr_bal, credit_limit, cash_credit_limit, open_date, expiration_date, reissue_date, curr_cyc_credit, curr_cyc_debit, addr_zip, group_id)
VALUES (10000000002, 'Y', 2500.00, 10000.00, 3000.00, '2021-06-01', '2026-06-01', '2024-06-01', 200.00, 800.00, '90210', 'STANDARD');

INSERT INTO cards (card_num, acct_id, cvv_cd, embossed_name, expiration_date, active_status)
VALUES ('4111111111111111', 10000000001, 0, 'JOHN M SMITH', '2025-01-15', 'Y');

INSERT INTO cards (card_num, acct_id, cvv_cd, embossed_name, expiration_date, active_status)
VALUES ('4222222222222222', 10000000002, 0, 'JANE A DOE', '2026-06-01', 'Y');

INSERT INTO card_xref (card_num, cust_id, acct_id)
VALUES ('4111111111111111', 1, 10000000001);

INSERT INTO card_xref (card_num, cust_id, acct_id)
VALUES ('4222222222222222', 2, 10000000002);

INSERT INTO transactions (tran_id, tran_type_cd, tran_cat_cd, tran_source, tran_desc, tran_amt, merchant_id, merchant_name, merchant_city, merchant_zip, card_num, orig_ts, proc_ts)
VALUES ('0000000000000001', '01', 5001, 'Online', 'Purchase at Amazon', 125.50, 100000001, 'Amazon.com', 'Seattle', '98101', '4111111111111111', '2024-01-15-10.30.00.000000', '2024-01-15-10.30.01.000000');

INSERT INTO transactions (tran_id, tran_type_cd, tran_cat_cd, tran_source, tran_desc, tran_amt, merchant_id, merchant_name, merchant_city, merchant_zip, card_num, orig_ts, proc_ts)
VALUES ('0000000000000002', '02', 5002, 'POS', 'Grocery Store Purchase', 67.89, 100000002, 'Whole Foods', 'New York', '10001', '4111111111111111', '2024-01-16-14.22.00.000000', '2024-01-16-14.22.01.000000');

INSERT INTO transactions (tran_id, tran_type_cd, tran_cat_cd, tran_source, tran_desc, tran_amt, merchant_id, merchant_name, merchant_city, merchant_zip, card_num, orig_ts, proc_ts)
VALUES ('0000000000000003', '01', 5001, 'Online', 'Electronics Purchase', 899.99, 100000003, 'Best Buy', 'Los Angeles', '90001', '4222222222222222', '2024-01-17-09.15.00.000000', '2024-01-17-09.15.01.000000');

INSERT INTO disclosure_groups (acct_group_id, tran_type_cd, tran_cat_cd, int_rate)
VALUES ('PREMIUM', '01', 5, 18.99);

INSERT INTO disclosure_groups (acct_group_id, tran_type_cd, tran_cat_cd, int_rate)
VALUES ('STANDARD', '01', 5, 24.99);

INSERT INTO disclosure_groups (acct_group_id, tran_type_cd, tran_cat_cd, int_rate)
VALUES ('DEFAULT', '01', 5, 21.99);

INSERT INTO user_security (usr_id, usr_fname, usr_lname, usr_pwd, usr_type)
VALUES ('admin01', 'System', 'Admin', 'CHANGEME', 'A');

INSERT INTO user_security (usr_id, usr_fname, usr_lname, usr_pwd, usr_type)
VALUES ('user0001', 'Regular', 'User', 'CHANGEME', 'R');
