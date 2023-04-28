DELETE
FROM users;
ALTER TABLE users
    ALTER COLUMN id RESTART WITH 1;
DELETE
FROM items;
ALTER TABLE items
    ALTER COLUMN id RESTART WITH 1;
DELETE
FROM bookings;
ALTER TABLE bookings
    ALTER COLUMN id RESTART WITH 1;

INSERT INTO users (name, email)
VALUES ('user1', 'user1@mail.ru'),
       ('user2', 'user2@mail.ru'),
       ('user3', 'user3@mail.ru');

INSERT INTO items (name, description, available, owner_id, request_id)
VALUES ('Дрель', 'Простая дрель', true, 1, null),
       ('Дрель', 'Дрель аккумуляторная', true, 2, null),
       ('Отвёртка', 'Отвёртка аккумуляторная', true, 3, null);

INSERT INTO bookings (status, start_time, end_time, item_id, booker_id)
VALUES ('WAITING', '2023-05-10T12:00:00', '2023-05-12T12:00:00', 1, 2),
       ('APPROVED', '2023-04-26T12:00:00', '2023-05-26T12:00:00', 2, 3),
       ('REJECTED', '2023-04-26T12:00:00', '2023-04-27T12:00:00', 1, 2),
       ('APPROVED', '2023-04-26T12:00:00', '2023-04-27T12:00:00', 1, 3);
