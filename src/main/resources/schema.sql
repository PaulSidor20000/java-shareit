drop table if exists item_requests;
drop table if exists comments;
drop table if exists bookings;
drop table if exists items;
drop table if exists users;

CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(255)                            NOT NULL,
    email VARCHAR(512)                            NOT NULL,
    CONSTRAINT pk_users_id
        PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL
        UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name        VARCHAR(255)                            NOT NULL,
    description VARCHAR(512)                            NOT NULL,
    available   BOOLEAN                                 NOT NULL,
    owner_id    BIGINT                                  NOT NULL,
    request_id  BIGINT,
    CONSTRAINT pk_items_id
        PRIMARY KEY (id),
    CONSTRAINT fk_items_owner_id
        FOREIGN KEY (owner_id)
            REFERENCES users (id)
            ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    status     VARCHAR(32)                             NOT NULL,
    start_time TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    end_time   TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    item_id    BIGINT                                  NOT NULL,
    booker_id  BIGINT                                  NOT NULL,
    CONSTRAINT pk_bookings_id
        PRIMARY KEY (id),
    CONSTRAINT fk_bookings_item_id
        FOREIGN KEY (item_id)
            REFERENCES items (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_bookings_booker_id
        FOREIGN KEY (booker_id)
            REFERENCES users (id)
            ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text        VARCHAR(255)                            NOT NULL,
    author_name VARCHAR(255)                            NOT NULL,
    created     TIMESTAMP                               NOT NULL,
    item_id     BIGINT                                  NOT NULL,
    booker_id   BIGINT                                  NOT NULL,
    CONSTRAINT pk_comments_id
        PRIMARY KEY (id),
    CONSTRAINT fk_comments_item_id
        FOREIGN KEY (item_id)
            REFERENCES items (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_comments_booker_id
        FOREIGN KEY (booker_id)
            REFERENCES users (id)
            ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS item_requests
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description VARCHAR(255)                            NOT NULL,
    created     TIMESTAMP                               NOT NULL,
    user_id     BIGINT                                  NOT NULL,
    CONSTRAINT pk_requests_id
        PRIMARY KEY (id),
    CONSTRAINT fk_item_requests_user_id
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE
);