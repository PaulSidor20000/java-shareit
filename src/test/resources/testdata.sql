DELETE
FROM comments;
ALTER TABLE comments
    ALTER COLUMN id RESTART WITH 1;
DELETE
FROM item_requests;
ALTER TABLE item_requests
    ALTER COLUMN id RESTART WITH 1;
DELETE
FROM bookings;
ALTER TABLE bookings
    ALTER COLUMN id RESTART WITH 1;
DELETE
FROM items;
ALTER TABLE items
    ALTER COLUMN id RESTART WITH 1;
DELETE
FROM users;
ALTER TABLE users
    ALTER COLUMN id RESTART WITH 1;

INSERT INTO users (name, email)
VALUES ('user1', 'user1@mail.ru'),
       ('user2', 'user2@mail.ru'),
       ('user3', 'user3@mail.ru');

INSERT INTO items (name, description, available, owner_id, request_id)
VALUES ('Item1', 'Item1 Description', true, 1, null),
       ('Item2', 'Item2 Description', true, 2, null),
       ('Item3 search', 'Item3 Description', true, 2, null),
       ('Item4', 'Item4 Description search', true, 3, 1);

INSERT INTO bookings (status, start_time, end_time, item_id, booker_id)
VALUES ('WAITING', '2023-08-10T12:00:00', '2023-09-10T12:00:00', 1, 2),
       ('REJECTED', '2023-04-26T12:00:00', '2023-04-27T12:00:00', 1, 2),
       ('APPROVED', '2023-04-26T12:00:00', '2023-04-27T12:00:00', 1, 3),
       ('APPROVED', '2023-05-10T12:00:00', '2023-06-10T12:00:00', 1, 2);

INSERT INTO item_requests (description, created, user_id)
VALUES ('Item description', '2023-08-10T12:00:00', 1),
       ('Item description2', '2023-08-10T12:00:00', 2);

INSERT INTO comments (text, author_name, created, item_id, booker_id)
VALUES ('Comment', 'user2', '2023-09-10T12:00:00', 1, 2);
