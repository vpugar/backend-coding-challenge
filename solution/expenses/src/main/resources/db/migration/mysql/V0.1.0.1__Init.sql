CREATE TABLE currency
(
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  short_name    VARCHAR(3)  NOT NULL,
  scale         SMALLINT      NOT NULL
)
  ENGINE = InnoDB;

CREATE UNIQUE INDEX currency_idx_01
  ON currency(short_name);

CREATE TABLE exchange_rate
(
  id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
  rate_date           DATE            NOT NULL,
  source_currency_id  BIGINT          NOT NULL,
  target_currency_id  BIGINT          NOT NULL,
  rate                DECIMAL(14, 8)  NOT NULL
)
  ENGINE = InnoDB;

CREATE UNIQUE INDEX exchange_rate_idx_01
  ON exchange_rate(rate_date, source_currency_id, target_currency_id);

ALTER TABLE exchange_rate
  ADD CONSTRAINT fk_exchange_rate_source_currency_id
  FOREIGN KEY (source_currency_id) REFERENCES currency(id);
ALTER TABLE exchange_rate
  ADD CONSTRAINT fk_exchange_rate_target_currency_id
  FOREIGN KEY (target_currency_id) REFERENCES currency(id);

CREATE TABLE expense_user
(
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  username      VARCHAR(255) NOT NULL,
  email         VARCHAR(255) NOT NULL,
  password      VARCHAR(255) NOT NULL
)
  ENGINE = InnoDB;

CREATE UNIQUE INDEX expense_user_idx_01
  ON expense_user(username);
CREATE UNIQUE INDEX expense_user_idx_02
  ON expense_user(email);

CREATE TABLE expense
(
  id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
  created_at          TIMESTAMP       NOT NULL,
  expense_date        DATE            NOT NULL,
  reason              VARCHAR(1000)   NOT NULL,
  source_currency_id  BIGINT          NOT NULL,
  source_amount       DECIMAL(14, 2)  NOT NULL,
  currency_id         BIGINT          NOT NULL,
  amount              DECIMAL(14, 2)  NOT NULL,
  vat_rate            DECIMAL(14, 2)  NOT NULL,
  vat_amount          DECIMAL(14, 2)  NOT NULL,
  user_id             BIGINT          NOT NULL
)
  ENGINE = InnoDB;

CREATE INDEX expense_idx_01
  ON expense(user_id);
CREATE INDEX expense_idx_02
  ON expense(expense_date);
CREATE INDEX expense_idx_03
  ON expense(created_at);

ALTER TABLE expense
  ADD CONSTRAINT fk_expense_source_currency_id
  FOREIGN KEY (source_currency_id) REFERENCES currency(id);
ALTER TABLE expense
  ADD CONSTRAINT fk_expense_currency_id
  FOREIGN KEY (currency_id) REFERENCES currency(id);
ALTER TABLE expense
  ADD CONSTRAINT fk_expense_user_id
  FOREIGN KEY (user_id) REFERENCES expense_user(id);
