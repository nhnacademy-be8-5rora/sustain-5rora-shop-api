-- Publisher 테이블에 데이터 삽입
INSERT INTO publishers (name) VALUES ('Test Publisher');

-- Book 테이블에 데이터 삽입
INSERT INTO books (title, regular_price, sale_price, stock, is_sale, isbn, contents, explanation, packaging, publish_date, publisher_id)
VALUES('Test Book', 10000, 8000, 200, TRUE, '123456789', 'Test contents of the book', 'Test explanation of the book', FALSE, '2024-12-16',1);

-- storage_infos 테이블에 데이터 삽입
INSERT INTO storage_infos(storage_name, storage_url)
VALUES ('storage1', 'storage_url_1');

-- BookImage 테이블에 데이터 삽입
INSERT INTO book_images (book_id, storage_id, file_path, is_thumbnail)
VALUES (1,1, 'image_path_1',FALSE);

INSERT INTO categories (name, parent_id, depth, display_order) VALUES ('국내도서', NULL, 0, 1);
INSERT INTO categories (name, parent_id, depth, display_order) VALUES ('소설/시/희곡', 1, 1, 1);
INSERT INTO categories (name, parent_id, depth, display_order) VALUES ('한국소설', 2, 2, 1);
INSERT INTO categories (name, parent_id, depth, display_order) VALUES ('한국 장편소설', 3, 3, 1);


INSERT INTO book_categories(category_id, book_id) VALUES(4, 1);

-- Review 테이블에 데이터 삽입
-- INSERT INTO reviews (review_content, review_rating, review_created_at, book_id, user_id)
-- VALUES ('Great Book', 5, '2024-12-16', 1, 'test');

-- -- Tag 테이블에 데이터 삽입
-- INSERT INTO tags (name)
-- VALUES ('Tag1');
--
-- -- BookTag 테이블에 데이터 삽입
-- INSERT INTO book_tags (book_id, tag_id)
-- VALUES (1, 1);
--
-- -- Like 테이블에 데이터 삽입
-- INSERT INTO likes (book_id, user_id, is_delete)
-- VALUES (1, 'test', TRUE);
