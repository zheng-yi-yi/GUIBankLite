-- 创建银行表
CREATE TABLE banks
(
    bank_id   INT AUTO_INCREMENT PRIMARY KEY,
    bank_name CHAR(8) NOT NULL
);

INSERT INTO banks ( bank_id, bank_name )
VALUES
    ( 1, '中国工商银行' );
INSERT INTO banks ( bank_id, bank_name )
VALUES
    ( 2, '中国建设银行' );
INSERT INTO banks ( bank_id, bank_name )
VALUES
    ( 3, '中国农业银行' );

-- 创建银行卡表
CREATE TABLE bankcards
(
    card_number CHAR(16) PRIMARY KEY,
    password    VARCHAR(32) NOT NULL,
    balance     DECIMAL(10, 2) NOT NULL,
    user_name   VARCHAR(20) NOT NULL,
    bank_id     INT,
    salt        VARCHAR(24) NOT NULL,
    FOREIGN KEY (bank_id) REFERENCES banks (bank_id)
);

-- 创建亲属关系表
CREATE TABLE relative_cards
(
    card_number          CHAR(16),
    relative_card_number CHAR(16),
    PRIMARY KEY (card_number, relative_card_number),
    FOREIGN KEY (card_number) REFERENCES bankcards (card_number),
    FOREIGN KEY (relative_card_number) REFERENCES bankcards (card_number)
);

-- 创建存款取款表
CREATE TABLE accounts
(
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    card_number    CHAR(16),
    operation_type CHAR(4) NOT NULL,
    amount         DECIMAL(10, 2) NOT NULL,
    operation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (card_number) REFERENCES bankcards (card_number)
);

-- 创建转账进账表
CREATE TABLE transactions
(
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    from_card_number CHAR(16),
    to_card_number CHAR(16),
    amount DECIMAL(10, 2) NOT NULL,
    operation_type CHAR(4) NOT NULL,
    operation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (from_card_number) REFERENCES bankcards (card_number),
    FOREIGN KEY (to_card_number) REFERENCES bankcards (card_number)
);