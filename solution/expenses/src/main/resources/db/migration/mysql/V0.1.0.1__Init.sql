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
  id                  BIGINT AUTO_INCREMENT PRIMARY KEY
)
  ENGINE = InnoDB;


CREATE TABLE expense_user
(
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  username      VARCHAR(255) NOT NULL,
  email         VARCHAR(255) NOT NULL
)
  ENGINE = InnoDB;

CREATE TABLE expense
(
  id                  BIGINT AUTO_INCREMENT PRIMARY KEY
)
  ENGINE = InnoDB;
