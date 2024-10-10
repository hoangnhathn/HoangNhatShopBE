CREATE USER hoangnhat IDENTIFIED BY 123456;
GRANT CONNECT, RESOURCE TO hoangnhat;
GRANT DBA TO hoangnhat;

BEGIN
   FOR t IN (SELECT table_name FROM all_tables WHERE owner = 'schema_name') LOOP
      EXECUTE IMMEDIATE 'GRANT SELECT, INSERT, UPDATE, DELETE ON schema_name.' || t.table_name || ' TO new_user';
   END LOOP;
END;
/

create table roles(
    id int PRIMARY KEY,
    name VARCHAR2(20) not null
);


create table users(
    id int primary key,
    fullname NVARCHAR2(100) DEFAULT '',
    phone_number VARCHAR2(10) not null,
    address VARCHAR2(200) DEFAULT '',
    password VARCHAR2(100) DEFAULT '' not null,
    created_at date,
    updated_at date,
    is_active NUMBER DEFAULT 1,
    date_of_birth DATE,
    facebook_account_id int DEFAULT 0,
    google_account_id int DEFAULT 0,
    role_id int,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE SEQUENCE users_seq
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;

CREATE OR REPLACE TRIGGER users_trigger
BEFORE INSERT ON users
FOR EACH ROW
WHEN (NEW.id IS NULL)
BEGIN
  SELECT users_seq.NEXTVAL INTO :NEW.id FROM dual;
END;
/

ALTER TABLE users add (role_id int);
ALTER TABLE users add (FOREIGN KEY (role_id) REFERENCES roles(id));






create table tokens(
    id int primary key,
    token VARCHAR2(255) UNIQUE not null,
    token_type VARCHAR2(50) not null,
    expiration_date DATE,
    revoked number(1) not null,
    expired number(1) not null,
    user_id int,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
CREATE SEQUENCE tokens_seq
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;

CREATE OR REPLACE TRIGGER tokens_trigger
BEFORE INSERT ON tokens
FOR EACH ROW
WHEN (NEW.id IS NULL)
BEGIN
  SELECT tokens_seq.NEXTVAL INTO :NEW.id FROM dual;
END;
/




create table social_accounts(
    id int primary key,
    provider VARCHAR2(20) not null,
    provider_id VARCHAR2(50) not null,
    email VARCHAR2(150) not null,
    name VARCHAR2(100) not null,
    user_id int,
    FOREIGN KEY (user_id) REFERENCES users(id)
)

CREATE SEQUENCE social_accounts_seq
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;

CREATE OR REPLACE TRIGGER social_accounts_trigger
BEFORE INSERT ON social_accounts
FOR EACH ROW
WHEN (NEW.id IS NULL)
BEGIN
  SELECT social_accounts_seq.NEXTVAL INTO :NEW.id FROM dual;
END;
/

//--------------------------------------------------------------------------------
    
create TABLE categories(
    id int primary key,
    name VARCHAR2(100) DEFAULT '' not null
);

CREATE SEQUENCE categories_seq
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;

CREATE OR REPLACE TRIGGER categories_trigger
BEFORE INSERT ON categories
FOR EACH ROW
WHEN (NEW.id IS NULL)
BEGIN
  SELECT categories_seq.NEXTVAL INTO :NEW.id FROM dual;
END;

DROP SEQUENCE categories_seq;

SELECT MAX(id) + 1 INTO new_start_value FROM categories;

DROP SEQUENCE categories_seq;

CREATE SEQUENCE categories_seq
START WITH new_start_value
INCREMENT BY 1
NOCACHE
NOCYCLE;


//------------------------------------------------------------------------------------------

create table products(
    id int primary key,
    name VARCHAR2(350),
    price float not null CHECK(price >=0),
    thumbnail VARCHAR2(300) DEFAULT '',
    description CLOB DEFAULT '',
    created_at date,
    updated_at date,
    category_id int,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE SEQUENCE products_seq
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;

CREATE OR REPLACE TRIGGER products_trigger
BEFORE INSERT ON products
FOR EACH ROW
WHEN (NEW.id IS NULL)
BEGIN
  SELECT products_seq.NEXTVAL INTO :NEW.id FROM dual;
END;

//---------------------------------------------------------------------------------------

create table product_images(
    id int  primary key,
    product_id int,
    CONSTRAINT fk_product_images_product_id FOREIGN KEY(product_id) REFERENCES products(id) ON DELETE CASCADE,
    image_url VARCHAR2(300)
);

CREATE SEQUENCE product_images_seq
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;

CREATE OR REPLACE TRIGGER product_images_trigger
BEFORE INSERT ON product_images
FOR EACH ROW
WHEN (NEW.id IS NULL)
BEGIN
  SELECT product_images_seq.NEXTVAL INTO :NEW.id FROM dual;
END;


UPDATE products
SET thumbnail = (
    SELECT image_url
    FROM product_images
    WHERE products.id = product_images.product_id 
    AND ROWNUM = 1
)
WHERE EXISTS (
    SELECT 1
    FROM product_images
    WHERE products.id = product_images.product_id 
);

    
//---------------------------------------------------------------------------------

create table orders(
    id int primary key,
    user_id int,
    FOREIGN KEY (user_id) REFERENCES users(id),
    fullname NVARCHAR2(100) DEFAULT '',
    email VARCHAR2(100) DEFAULT '',
    phone_number VARCHAR2(10) not null,
    address NVARCHAR2(200) not null,
    note NVARCHAR2(100) DEFAULT '',
    order_date DATE DEFAULT SYSDATE,
    status VARCHAR2(20),
    total_money float CHECK(total_money >= 0),
    shipping_method VARCHAR(100),
    shipping_address VARCHAR(200),
    shipping_date DATE,
    tracking_number VARCHAR(100),
    payment_method VARCHAR(100),
    active NUMBER(1),
    CONSTRAINT status_check CHECK (status IN ('pending', 'processing', 'shipped', 'delivered', 'cancelled'))
);


CREATE SEQUENCE orders_seq
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;

CREATE OR REPLACE TRIGGER orders_trigger
BEFORE INSERT ON orders
FOR EACH ROW
WHEN (NEW.id IS NULL)
BEGIN
  SELECT orders_seq.NEXTVAL INTO :NEW.id FROM dual;
END;
//-----------------------------------------------------------------------------

create table order_details(
    id int primary key,
    order_id int,
    foreign key (order_id) references orders(id),
    product_id int,
    foreign key (product_id) references products(id),
    price float check(price>=0),
    number_of_products int check(number_of_products>0),
    total_money float check(total_money >= 0),
    color varchar2(20) default ''
);

CREATE SEQUENCE order_details_seq
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;

CREATE OR REPLACE TRIGGER order_details_trigger
BEFORE INSERT ON order_details
FOR EACH ROW
WHEN (NEW.id IS NULL)
BEGIN
  SELECT order_details_seq.NEXTVAL INTO :NEW.id FROM dual;
END;

-----------------------------------------------------------------------------
create table slides(
    id int primary key,
    image_url varchar2(255),
    link varchar2(255)
);

CREATE SEQUENCE slides_seq
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;

CREATE OR REPLACE TRIGGER slides_trigger
BEFORE INSERT ON slides
FOR EACH ROW
WHEN (NEW.id IS NULL)
BEGIN
  SELECT slides_seq.NEXTVAL INTO :NEW.id FROM dual;
END;

-----------------------------------------------------------------------------

CREATE TABLE reviews (
    id int PRIMARY KEY,
    product_id int,
    user_id int,  
    rating int CHECK(rating >= 1 AND rating <= 5),  
    comment_user VARCHAR2(255) DEFAULT '',  
    created_at DATE DEFAULT CURRENT_DATE,
    updated_at DATE DEFAULT CURRENT_DATE,
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE SEQUENCE reviews_seq
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;

CREATE OR REPLACE TRIGGER reviews_trigger
BEFORE INSERT ON reviews
FOR EACH ROW
WHEN (NEW.id IS NULL)
BEGIN
  SELECT reviews_seq.NEXTVAL INTO :NEW.id FROM dual;
END;



DROP TABLE reviews;
drop table order_details;
drop table orders;
drop table product_images;
drop table products;
drop table categories;
drop table social_accounts;
drop table tokens;
drop table users;
drop table roles;
drop table slides;
