SET @current_date = NOW();

insert into account(first_name, last_name, email, password, gender, account_type, created_at, updated_at)
values ('Ivan', 'Tsenov', 'ivan.t.tsenov@gmail.com', SHA2('IVNtsn963', 512), 'MALE', 'REAL', @current_date, @current_date);

insert into account(first_name, last_name, email, password, gender, account_type, created_at, updated_at)
values ('Toni', 'Tsenova', 'toni.k.tsenova@gmail.com', SHA2('TNSLVtsn963', 512), 'FEMALE', 'REAL', @current_date, @current_date);