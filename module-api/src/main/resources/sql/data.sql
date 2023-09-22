INSERT INTO USERS(ID, EMAIL, PASSWORD, NICKNAME, USER_ROLE, ACCOUNT, ZIPCODE, ADDRESS, ADDRESS_DETAIL) VALUES
(1, 'aaa@email.com', '$2a$12$t8iA0oBSyN9vpWUEB1o2cef0IlQp0wBTX0xAXj5sOk7l1CVQyovpG', 'KIM', 'ROLE_USER', 500000, '00001', 'SEOUL', 'JONGRO'),
(2, 'bbb@email.com', '$2a$12$q5AzUVup5.jaOuegNlG.JOlsT6YvbW8WNE9f/0pt7zIaPyt41Yunq', 'PARK', 'ROLE_ADMIN', 400000, '00002', 'INCHEON', 'YEONSU');


INSERT INTO BRANDS(ID, BRAND_NAME) VALUES
(1, 'NIKE'),
(2, 'ADIDAS');

INSERT INTO PRODUCTS(ID, BRAND_ID, PRODUCT_NAME, MODEL_NUMBER, RELEASE_DATE, COLOR, RELEASE_PRICE, SIZE) VALUES
(1, 1, 'NIKE_1', 'BLK-0001', NOW(), 'BLACK/WHITE', '150000', 255),
(2, 1, 'NIKE_1', 'BLK-0001', NOW(), 'BLACK/WHITE', '150000', 260),
(3, 1, 'NIKE_1', 'BLK-0001', NOW(), 'BLACK/WHITE', '150000', 265),
(4, 1, 'NIKE_1', 'BLK-0001', NOW(), 'BLACK/WHITE', '150000', 270),
(5, 1, 'NIKE_1', 'BLK-0001', NOW(), 'BLACK/WHITE', '150000', 275),
(6, 1, 'NIKE_1', 'BLK-0001', NOW(), 'BLACK/WHITE', '150000', 280),
(7, 1, 'NIKE_2', 'ORG-0001', NOW(), 'ORANGE', '200000', 260);
