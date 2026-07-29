-- CardDemo PostgreSQL Schema
-- Migrated from COBOL VSAM KSDS copybooks

-- Users table (from CSUSR01Y.cpy - SEC-USER-DATA, 80-byte record)
CREATE TABLE IF NOT EXISTS users (
    user_id         VARCHAR(8) PRIMARY KEY,
    first_name      VARCHAR(20) NOT NULL DEFAULT '',
    last_name       VARCHAR(20) NOT NULL DEFAULT '',
    password_hash   VARCHAR(128) NOT NULL,
    user_type       CHAR(1) NOT NULL DEFAULT 'U' CHECK (user_type IN ('A', 'U')),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Customers table (from CVCUS01Y.cpy - CUSTOMER-RECORD, 500-byte record)
CREATE TABLE IF NOT EXISTS customers (
    cust_id               SERIAL PRIMARY KEY,
    first_name            VARCHAR(25) NOT NULL DEFAULT '',
    middle_name           VARCHAR(25) NOT NULL DEFAULT '',
    last_name             VARCHAR(25) NOT NULL DEFAULT '',
    addr_line_1           VARCHAR(50) NOT NULL DEFAULT '',
    addr_line_2           VARCHAR(50) NOT NULL DEFAULT '',
    addr_line_3           VARCHAR(50) NOT NULL DEFAULT '',
    addr_state_cd         CHAR(2) NOT NULL DEFAULT '',
    addr_country_cd       CHAR(3) NOT NULL DEFAULT 'USA',
    addr_zip              VARCHAR(10) NOT NULL DEFAULT '',
    phone_num_1           VARCHAR(15) NOT NULL DEFAULT '',
    phone_num_2           VARCHAR(15) NOT NULL DEFAULT '',
    ssn                   VARCHAR(9) NOT NULL DEFAULT '',
    govt_issued_id        VARCHAR(20) NOT NULL DEFAULT '',
    dob                   DATE,
    eft_account_id        VARCHAR(10) NOT NULL DEFAULT '',
    pri_card_holder_ind   CHAR(1) NOT NULL DEFAULT 'Y',
    fico_credit_score     INTEGER NOT NULL DEFAULT 0 CHECK (fico_credit_score BETWEEN 0 AND 850),
    created_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Accounts table (from CVACT01Y.cpy - ACCOUNT-RECORD, 300-byte record)
CREATE TABLE IF NOT EXISTS accounts (
    acct_id               BIGINT PRIMARY KEY,
    active_status         CHAR(1) NOT NULL DEFAULT 'Y' CHECK (active_status IN ('Y', 'N')),
    curr_bal              NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    credit_limit          NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    cash_credit_limit     NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    open_date             DATE,
    expiration_date       DATE,
    reissue_date          DATE,
    curr_cyc_credit       NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    curr_cyc_debit        NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    addr_zip              VARCHAR(10) NOT NULL DEFAULT '',
    group_id              VARCHAR(10) NOT NULL DEFAULT '',
    created_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Cards table (from CVACT02Y.cpy - CARD-RECORD, 150-byte record)
CREATE TABLE IF NOT EXISTS cards (
    card_num              VARCHAR(16) PRIMARY KEY,
    acct_id               BIGINT NOT NULL REFERENCES accounts(acct_id),
    cvv_cd                VARCHAR(3) NOT NULL DEFAULT '',
    embossed_name         VARCHAR(50) NOT NULL DEFAULT '',
    expiration_date       DATE,
    active_status         CHAR(1) NOT NULL DEFAULT 'Y' CHECK (active_status IN ('Y', 'N')),
    created_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Card-Account-Customer cross-reference (from CVACT03Y.cpy, 50-byte record)
CREATE TABLE IF NOT EXISTS card_xref (
    xref_card_num         VARCHAR(16) PRIMARY KEY,
    xref_cust_id          INTEGER NOT NULL REFERENCES customers(cust_id),
    xref_acct_id          BIGINT NOT NULL REFERENCES accounts(acct_id)
);

-- Transaction types (from CVTRA03Y.cpy, 60-byte record)
CREATE TABLE IF NOT EXISTS transaction_types (
    tran_type_cd          CHAR(2) PRIMARY KEY,
    tran_type_desc        VARCHAR(50) NOT NULL DEFAULT ''
);

-- Transaction categories (from CVTRA04Y.cpy, 60-byte record)
CREATE TABLE IF NOT EXISTS transaction_categories (
    tran_type_cd          CHAR(2) NOT NULL REFERENCES transaction_types(tran_type_cd),
    tran_cat_cd           INTEGER NOT NULL,
    tran_cat_desc         VARCHAR(50) NOT NULL DEFAULT '',
    PRIMARY KEY (tran_type_cd, tran_cat_cd)
);

-- Transactions table (from CVTRA05Y.cpy - TRAN-RECORD, 350-byte record)
CREATE TABLE IF NOT EXISTS transactions (
    tran_id               VARCHAR(16) PRIMARY KEY,
    tran_type_cd          CHAR(2) NOT NULL DEFAULT '',
    tran_cat_cd           INTEGER NOT NULL DEFAULT 0,
    tran_source           VARCHAR(10) NOT NULL DEFAULT '',
    tran_desc             VARCHAR(100) NOT NULL DEFAULT '',
    tran_amt              NUMERIC(11,2) NOT NULL DEFAULT 0.00,
    merchant_id           BIGINT NOT NULL DEFAULT 0,
    merchant_name         VARCHAR(50) NOT NULL DEFAULT '',
    merchant_city         VARCHAR(50) NOT NULL DEFAULT '',
    merchant_zip          VARCHAR(10) NOT NULL DEFAULT '',
    card_num              VARCHAR(16) NOT NULL DEFAULT '',
    orig_ts               TIMESTAMP,
    proc_ts               TIMESTAMP,
    created_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Transaction category balances (from CVTRA01Y.cpy, 50-byte record)
CREATE TABLE IF NOT EXISTS tran_cat_bal (
    acct_id               BIGINT NOT NULL REFERENCES accounts(acct_id),
    tran_type_cd          CHAR(2) NOT NULL,
    tran_cat_cd           INTEGER NOT NULL,
    cat_bal               NUMERIC(11,2) NOT NULL DEFAULT 0.00,
    PRIMARY KEY (acct_id, tran_type_cd, tran_cat_cd)
);

-- Disclosure groups / interest rates (from CVTRA02Y.cpy, 50-byte record)
CREATE TABLE IF NOT EXISTS disclosure_groups (
    acct_group_id         VARCHAR(10) NOT NULL,
    tran_type_cd          CHAR(2) NOT NULL,
    tran_cat_cd           INTEGER NOT NULL,
    interest_rate         NUMERIC(6,2) NOT NULL DEFAULT 0.00,
    PRIMARY KEY (acct_group_id, tran_type_cd, tran_cat_cd)
);

-- Indexes for common query patterns
CREATE INDEX IF NOT EXISTS idx_cards_acct_id ON cards(acct_id);
CREATE INDEX IF NOT EXISTS idx_card_xref_cust_id ON card_xref(xref_cust_id);
CREATE INDEX IF NOT EXISTS idx_card_xref_acct_id ON card_xref(xref_acct_id);
CREATE INDEX IF NOT EXISTS idx_transactions_card_num ON transactions(card_num);
CREATE INDEX IF NOT EXISTS idx_transactions_orig_ts ON transactions(orig_ts);
CREATE INDEX IF NOT EXISTS idx_tran_cat_bal_acct ON tran_cat_bal(acct_id);
