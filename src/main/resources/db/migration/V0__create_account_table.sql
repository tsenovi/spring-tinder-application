CREATE TABLE account (
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    first_name varchar(255)       not null,
    last_name varchar(255)        not null,
    email varchar(255)            not null,
    password varchar(255)         not null,
    gender varchar(255)           not null,
    created_at varchar(255)       not null,
    updated_at varchar(255)       not null
)