CREATE TABLE account (
    id INT NOT NULL AUTO_INCREMENT,
    first_name varchar(255)       not null,
    last_name varchar(255)        not null,
    email varchar(255)            not null,
    password varchar(255)         not null,
    gender ENUM('MALE', 'FEMALE') not null,
    created_at TIMESTAMP          not null,
    updated_at TIMESTAMP          not null,
    PRIMARY KEY (id)
)