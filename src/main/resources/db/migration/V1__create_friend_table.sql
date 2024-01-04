CREATE TABLE friend (
    account_id INT NOT NULL,
    friend_id INT NOT NULL,
    PRIMARY KEY (account_id, friend_id),
    FOREIGN KEY (account_id) REFERENCES account(id),
    FOREIGN KEY (friend_id) REFERENCES account(id)
);