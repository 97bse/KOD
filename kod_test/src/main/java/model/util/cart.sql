CREATE TABLE CART(
	CART_ID 			INT				PRIMARY KEY,
	CART_PRODUCT_CNT	INT	DEFAULT 0	NOT NULL,
	MEMBER_ID 			VARCHAR(20)		REFERENCES MEMBER(MEMBER_ID),
	PRODUCT_ID 			INT 			REFERENCES PRODUCT(PRODUCT_ID)
);

SELECT * FROM CART;

DROP TABLE CART;