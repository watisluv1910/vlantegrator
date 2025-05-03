create user if not exists '$DB_USER'@'%' identified by '$DB_PASSWORD';
grant all privileges on $DB_SCHEMA.* to '$DB_USER'@'%';
flush privileges;