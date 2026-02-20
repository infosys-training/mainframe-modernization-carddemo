CREATE TABLE IF NOT EXISTS customers (
    cust_id           BIGINT PRIMARY KEY,
    first_name        VARCHAR(25),
    middle_name       VARCHAR(25),
    last_name         VARCHAR(25),
    addr_line_1       VARCHAR(50),
    addr_line_2       VARCHAR(50),
    addr_line_3       VARCHAR(50),
    addr_state_cd     VARCHAR(2),
    addr_country_cd   VARCHAR(3),
    addr_zip          VARCHAR(10),
    phone_num_1       VARCHAR(15),
    phone_num_2       VARCHAR(15),
    ssn               BIGINT,
    govt_issued_id    VARCHAR(20),
    dob_yyyy_mm_dd    VARCHAR(10),
    eft_account_id    VARCHAR(10),
    pri_card_holder_ind VARCHAR(1),
    fico_credit_score INTEGER
);

CREATE TABLE IF NOT EXISTS accounts (
    acct_id           BIGINT PRIMARY KEY,
    active_status     VARCHAR(1),
    curr_bal          DECIMAL(12,2),
    credit_limit      DECIMAL(12,2),
    cash_credit_limit DECIMAL(12,2),
    open_date         VARCHAR(10),
    expiration_date   VARCHAR(10),
    reissue_date      VARCHAR(10),
    curr_cyc_credit   DECIMAL(12,2),
    curr_cyc_debit    DECIMAL(12,2),
    addr_zip          VARCHAR(10),
    group_id          VARCHAR(10)
);

CREATE TABLE IF NOT EXISTS cards (
    card_num          VARCHAR(16) PRIMARY KEY,
    acct_id           BIGINT,
    cvv_cd            INTEGER,
    embossed_name     VARCHAR(50),
    expiration_date   VARCHAR(10),
    active_status     VARCHAR(1)
);

CREATE TABLE IF NOT EXISTS card_xref (
    card_num          VARCHAR(16) PRIMARY KEY,
    cust_id           BIGINT,
    acct_id           BIGINT
);

CREATE TABLE IF NOT EXISTS transactions (
    tran_id           VARCHAR(16) PRIMARY KEY,
    tran_type_cd      VARCHAR(2),
    tran_cat_cd       INTEGER,
    tran_source       VARCHAR(10),
    tran_desc         VARCHAR(100),
    tran_amt          DECIMAL(11,2),
    merchant_id       BIGINT,
    merchant_name     VARCHAR(50),
    merchant_city     VARCHAR(50),
    merchant_zip      VARCHAR(10),
    card_num          VARCHAR(16),
    orig_ts           VARCHAR(26),
    proc_ts           VARCHAR(26)
);

CREATE TABLE IF NOT EXISTS tran_cat_balances (
    acct_id           BIGINT,
    tran_type_cd      VARCHAR(2),
    tran_cat_cd       INTEGER,
    tran_cat_bal      DECIMAL(11,2),
    PRIMARY KEY (acct_id, tran_type_cd, tran_cat_cd)
);

CREATE TABLE IF NOT EXISTS disclosure_groups (
    acct_group_id     VARCHAR(10),
    tran_type_cd      VARCHAR(2),
    tran_cat_cd       INTEGER,
    int_rate          DECIMAL(6,2),
    PRIMARY KEY (acct_group_id, tran_type_cd, tran_cat_cd)
);

CREATE TABLE IF NOT EXISTS user_security (
    usr_id            VARCHAR(8) PRIMARY KEY,
    usr_fname         VARCHAR(20),
    usr_lname         VARCHAR(20),
    usr_pwd           VARCHAR(8),
    usr_type          VARCHAR(1)
);

CREATE INDEX IF NOT EXISTS idx_cards_acct_id ON cards(acct_id);
CREATE INDEX IF NOT EXISTS idx_card_xref_acct_id ON card_xref(acct_id);
CREATE INDEX IF NOT EXISTS idx_transactions_card_num ON transactions(card_num);
CREATE INDEX IF NOT EXISTS idx_tran_cat_bal_acct ON tran_cat_balances(acct_id);
