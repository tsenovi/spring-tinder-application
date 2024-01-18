CREATE TABLE verification_token (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  token VARCHAR(36) NOT NULL,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  expires_at TIMESTAMP NOT NULL,
  verified_at TIMESTAMP DEFAULT NULL,
  account_id BIGINT NOT NULL,
  FOREIGN KEY (account_id) REFERENCES account(id)
);
