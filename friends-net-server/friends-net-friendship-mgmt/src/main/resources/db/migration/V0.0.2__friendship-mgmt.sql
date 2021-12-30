CREATE TABLE relationship_status(
    id SERIAL PRIMARY KEY,
    name varchar(30) NOT NULL UNIQUE
);

CREATE TABLE user_relationship(
    id_sender int NOT NULL REFERENCES auth_user(id),
    id_receiver int NOT NULL REFERENCES auth_user(id),
    id_status int NOT NULL REFERENCES relationship_status(id),
    created_at date NOT NULL,
    last_updated date NOT NULL,
    UNIQUE (id_sender, id_receiver),
    UNIQUE (id_receiver, id_sender)
);