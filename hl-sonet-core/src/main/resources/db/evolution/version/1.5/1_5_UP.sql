insert into users (uuid, email, first_name, last_name, gender, birthdate, city, biography, role, password, age)
values ('50109079-8dec-4847-868e-717c77deb881', 'admin@sonet.com', 'admin', 'admin', 'M', '01.01.1950', 'admin', 'admin', 'ADMIN', '$2a$12$f4S1O26MA8q9LcJkSJyZKuHA0IldecXALqfSG3L/cjwm4QEv5ki8C', 100)
returning id, uuid;

