CREATE TABLE rating (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  account_id BIGINT NOT NULL,
  friend_id BIGINT NOT NULL,
  rating INT NOT NULL,
  FOREIGN KEY (account_id) REFERENCES account(id),
  FOREIGN KEY (friend_id) REFERENCES account(id)
);
