CREATE TABLE post (
    id SERIAL primary key,
    author_id int not null references auth_user(id),
    title varchar(255) not null ,
    content Text not null ,
    date_created timestamp not null,
    announcement bool not null default false
);