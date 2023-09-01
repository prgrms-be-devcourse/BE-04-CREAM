INSERT INTO BRANDS(ID, BRAND_NAME) VALUES (1, 'NIKE'), (2, 'ADIDAS');

INSERT INTO PRODUCTS(ID, BRAND_ID, PRODUCT_NAME, MODEL_NUMBER, RELEASE_DATE, COLOR, RELEASE_PRICE) VALUES
(1, 1, 'NIKE_1', 'BLK-0001', NOW(), 'BLACK/WHITE', '150000'),
(2, 1, 'NIKE_2', 'ORG-0001', NOW(), 'ORANGE', '200000');

INSERT INTO SIZED_PRODUCTS(ID, PRODUCT_ID, SIZE) VALUES
(1, 1, 250) ,(2, 1, 260), (3, 1, 270), (4, 2, 250) ,(5, 2, 260), (6, 2, 270);

INSERT INTO USERS(ID, EMAIL, PASSWORD, NICKNAME, USER_ROLE, ACCOUNT, ZIPCODE, ADDRESS, ADDRESS_DETAIL) VALUES
(1, 'aaa@email.com', 'aaa', 'A', 'ROLE_USER', 500000, '12345', 'SEOUL', 'detail'),
(2, 'bbb@email.com', 'bbb', 'B', 'ROLE_USER', 200000, '54321', 'DAEGU', 'detail'),
(3, 'ccc@email.com', 'ccc', 'C', 'ROLE_USER', 100000, '67890', 'DAEGU', 'detail');

INSERT INTO PURCHASE_BIDDINGS(ID, PURCHASE_BIDDER_ID, SIZED_PRODUCT_ID, PRICE, STATUS, START_DATE, DUE_DATE) VALUES
(1, 1, 1, 180000, 'LIVE', NOW(), TIMESTAMPADD(DAY, 7, NOW())),
(2, 2, 1, 150000, 'LIVE', NOW(), TIMESTAMPADD(DAY, 7, NOW()));

INSERT INTO SELL_BIDDINGS(ID, SELL_BIDDER_ID, SIZED_PRODUCT_ID, PRICE, STATUS, START_DATE, DUE_DATE) VALUES
(1, 3, 1, 200000, 'LIVE', NOW(), TIMESTAMPADD(DAY, 7, NOW())),
(2, 3, 1, 210000, 'LIVE', NOW(), TIMESTAMPADD(DAY, 7, NOW())),
(3, 3, 2, 100000, 'LIVE', NOW(), TIMESTAMPADD(DAY, 7, NOW())),
(4, 3, 4, 120000, 'LIVE', NOW(), TIMESTAMPADD(DAY, 7, NOW())),
(5, 3, 4, 130000, 'LIVE', NOW(), TIMESTAMPADD(DAY, 7, NOW()));
