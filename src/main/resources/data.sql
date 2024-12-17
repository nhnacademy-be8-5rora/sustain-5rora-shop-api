INSERT INTO users (
    id,
    user_pwd,
    user_name,
    user_birthday,
    user_phone_number,
    user_email,
    user_status,
    user_last_login,
    user_signup_date,
    is_oauth
)
VALUES (
           'user123',
           'password123',
           'John Doe',
           '1990-01-01',
           '010-1234-5678',
           'john.doe@example.com',
           'ACTIVE',
           '2024-12-12 14:30:00',
           '2024-12-12',
           TRUE
       ),
       (
       'user1234',
       'password123',
       'John Doe',
       '1990-01-01',
       '010-1234-5678',
       'john.doe@example.com',
       'ACTIVE',
       '2024-12-12 14:30:00',
       '2024-12-12',
       TRUE
   );

INSERT INTO roles (role_name) VALUES ('ROLE_ADMIN');
INSERT INTO roles (role_name) VALUES ('ROLE_USER');

INSERT INTO user_roles (user_id, role_id) VALUES ('user123', 1);

-- ///////////////////////// book ////////////////////////////

INSERT INTO publishers (name)
VALUES ('Penguin Books');

insert into serieses (name)
values ('test name');

-- 최상위 카테고리(부모가 없는 카테고리) 삽입
INSERT INTO categories (name, parent_id, depth) VALUES ('Example Category', NULL, 0);
INSERT INTO categories (name, parent_id, depth) VALUES ('Example Category2', NULL, 0);

insert into books (title, regular_price, sale_price, is_sale, explanation, publisher_id, series_id, packaging, publish_date, isbn, stock, contents)
values ('test title', 10000, 9000, TRUE, 'test desc', 1, 1, FALSE, '2024-12-12', '1234567890123', 100, 'sample contents');

insert into books (title, regular_price, sale_price, is_sale, explanation, publisher_id, series_id, packaging, publish_date, isbn, stock, contents)
values ('test title2', 10000, 9000, TRUE, 'test desc2', 1, 1, FALSE, '2024-12-12', '1234567890124', 100, 'sample contents2');

-- BookCategory 엔티티 삽입
INSERT INTO book_categories (category_id, book_id) VALUES (1, 1);
INSERT INTO book_categories (category_id, book_id) VALUES (2, 1);

-- Author 엔티티 삽입
INSERT INTO authors (name)
VALUES ('example author'),
       ('example editor');

-- AuthorRole 엔티티 삽입
INSERT INTO author_roles (role)
VALUES ('AUTHOR'),
       ('EDITOR');

-- book_authors 테이블에 데이터 삽입
INSERT INTO book_authors (author_id, author_role_id, book_id)
VALUES (1, 1, 1),  -- John Doe is the AUTHOR of the book with ID 1
       (2, 2, 1),  -- Jane Smith is the EDITOR of the book with ID 1
       (2,1,2);

--  book_views 데이터 삽입
INSERT INTO book_views (book_id, user_ip, user_id)
VALUES
    (1, '192.168.1.1', 'user123'),  -- Book ID 1 viewed by user123 with IP 192.168.1.1
    (1, '192.168.1.2', 'user1234'), -- Book ID 1 viewed by user1234 with IP 192.168.1.2
    (2, '192.168.1.3', 'user123');  -- Book ID 2 viewed by user123 with IP 192.168.1.3
