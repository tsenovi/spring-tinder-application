CREATE TABLE verification_token (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  token VARCHAR(36) NOT NULL,
  created_at timestamp,
  updated_at timestamp,
  expires_at timestamp NOT NULL,
  verified_at timestamp DEFAULT NULL,
  account_id BIGINT NOT NULL,
  FOREIGN KEY (account_id) REFERENCES account(id)
);