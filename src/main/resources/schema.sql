DROP TABLE IF EXISTS genre_line CASCADE;
DROP TABLE IF EXISTS genres CASCADE;
DROP TABLE IF EXISTS films CASCADE;
DROP TABLE IF EXISTS ratings CASCADE;
DROP TABLE IF EXISTS likes CASCADE;
DROP TABLE IF EXISTS friendship_line CASCADE;
DROP TABLE IF EXISTS friendships CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE users (
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255) NOT NULL UNIQUE,
    login VARCHAR(255) NOT NULL UNIQUE,
    birthday DATE
);

CREATE TABLE friendships (
    friendship_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    status INT NOT NULL
);

CREATE TABLE friendship_line (
    friendship_line_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user1_id BIGINT NOT NULL,
    user2_id BIGINT NOT NULL,
    friendship_id BIGINT NOT NULL,
    CONSTRAINT fk_user1 FOREIGN KEY (user1_id) REFERENCES users (user_id),
    CONSTRAINT fk_user2 FOREIGN KEY (user2_id) REFERENCES users (user_id),
    CONSTRAINT fk_friendship FOREIGN KEY (friendship_id) REFERENCES friendships (friendship_id),
    CONSTRAINT uq_users_pair UNIQUE (user1_id, user2_id)
);

CREATE TABLE ratings (
    rating_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    rating_name VARCHAR(255) NOT NULL
);

CREATE TABLE films (
    film_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    release_date DATE,
    duration INT,
    rating_id BIGINT,
    CONSTRAINT fk_rating FOREIGN KEY (rating_id) REFERENCES ratings (rating_id)
);

CREATE TABLE genres (
    genre_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    genre_name VARCHAR(255) NOT NULL
);

CREATE TABLE genre_line (
    genre_line_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_id BIGINT NOT NULL,
    genre_id BIGINT NOT NULL,
    CONSTRAINT fk_film FOREIGN KEY (film_id) REFERENCES films (film_id),
    CONSTRAINT fk_genre FOREIGN KEY (genre_id) REFERENCES genres (genre_id),
    CONSTRAINT uq_film_genre UNIQUE (film_id, genre_id)
);

CREATE TABLE likes (
    like_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    film_id BIGINT NOT NULL,
    CONSTRAINT fk_like_user FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT fk_like_film FOREIGN KEY (film_id) REFERENCES films (film_id),
    CONSTRAINT uq_user_film UNIQUE (user_id, film_id)
);

INSERT INTO ratings (rating_name) VALUES
('G'),
('PG'),
('PG-13'),
('R'),
('NC-17');

INSERT INTO genres (genre_name) VALUES
('Комедия'),
('Драма'),
('Мультфильм'),
('Триллер'),
('Документальный'),
('Боевик');
