ALTER TABLE account
ADD COLUMN location_id BIGINT,
ADD CONSTRAINT fk_account_location
FOREIGN KEY (location_id) REFERENCES location (id);