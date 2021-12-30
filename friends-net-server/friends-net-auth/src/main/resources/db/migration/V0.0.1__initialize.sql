CREATE TABLE auth_user(
    id SERIAL PRIMARY KEY,
    login varchar(50) UNIQUE NOT NULL,
    password varchar(70) NOT NULL,
    name varchar(50) NOT NULL
);

CREATE TABLE auth_role(
    id SERIAL PRIMARY KEY,
    name varchar(15) UNIQUE NOT NULL
);

CREATE TABLE auth_user_role(
    id_user int NOT NULL REFERENCES auth_user(id),
    id_role int NOT NULL REFERENCES auth_role(id),
    UNIQUE (id_user, id_role)
);