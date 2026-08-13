-- Database: drink_order_db
-- Giai đoạn 1 - DDL tham khảo (chạy trước khi start Spring Boot hoặc dùng ddl-auto=update)

CREATE DATABASE IF NOT EXISTS drink_order_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE drink_order_db;

CREATE TABLE users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  full_name VARCHAR(150) NOT NULL,
  phone VARCHAR(20) NULL,
  role ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
  enabled BOOLEAN NOT NULL DEFAULT TRUE,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE categories (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(500) NULL,
  image_url VARCHAR(500) NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE products (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  category_id BIGINT NOT NULL,
  name VARCHAR(200) NOT NULL,
  description TEXT NULL,
  image_url VARCHAR(500) NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE product_sizes (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  product_id BIGINT NOT NULL,
  size_name VARCHAR(20) NOT NULL,
  price DECIMAL(12, 2) NOT NULL,
  CONSTRAINT fk_sizes_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
  UNIQUE KEY uk_product_size (product_id, size_name)
);

CREATE TABLE toppings (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  price DECIMAL(12, 2) NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE product_toppings (
  product_id BIGINT NOT NULL,
  topping_id BIGINT NOT NULL,
  PRIMARY KEY (product_id, topping_id),
  CONSTRAINT fk_pt_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
  CONSTRAINT fk_pt_topping FOREIGN KEY (topping_id) REFERENCES toppings(id) ON DELETE CASCADE
);

CREATE TABLE addresses (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  recipient_name VARCHAR(150) NOT NULL,
  phone VARCHAR(20) NOT NULL,
  address_line VARCHAR(500) NOT NULL,
  is_default BOOLEAN NOT NULL DEFAULT FALSE,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_addresses_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE cart (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL UNIQUE,
  CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE cart_items (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  cart_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  product_size_id BIGINT NOT NULL,
  quantity INT NOT NULL DEFAULT 1,
  note VARCHAR(255) NULL,
  CONSTRAINT fk_ci_cart FOREIGN KEY (cart_id) REFERENCES cart(id) ON DELETE CASCADE,
  CONSTRAINT fk_ci_product FOREIGN KEY (product_id) REFERENCES products(id),
  CONSTRAINT fk_ci_size FOREIGN KEY (product_size_id) REFERENCES product_sizes(id)
);

CREATE TABLE cart_item_toppings (
  cart_item_id BIGINT NOT NULL,
  topping_id BIGINT NOT NULL,
  PRIMARY KEY (cart_item_id, topping_id),
  CONSTRAINT fk_cit_item FOREIGN KEY (cart_item_id) REFERENCES cart_items(id) ON DELETE CASCADE,
  CONSTRAINT fk_cit_topping FOREIGN KEY (topping_id) REFERENCES toppings(id)
);

CREATE TABLE orders (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  address_id BIGINT NOT NULL,
  status ENUM('PENDING', 'CONFIRMED', 'PREPARING', 'DELIVERING', 'DELIVERED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
  payment_method ENUM('COD', 'BANK_TRANSFER') NOT NULL,
  payment_status ENUM('UNPAID', 'PAID') NOT NULL DEFAULT 'UNPAID',
  subtotal DECIMAL(12, 2) NOT NULL,
  total DECIMAL(12, 2) NOT NULL,
  note VARCHAR(500) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_orders_address FOREIGN KEY (address_id) REFERENCES addresses(id)
);

CREATE TABLE order_items (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  product_name VARCHAR(200) NOT NULL,
  size_name VARCHAR(20) NOT NULL,
  unit_price DECIMAL(12, 2) NOT NULL,
  quantity INT NOT NULL,
  line_total DECIMAL(12, 2) NOT NULL,
  CONSTRAINT fk_oi_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
  CONSTRAINT fk_oi_product FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE order_item_toppings (
  order_item_id BIGINT NOT NULL,
  topping_name VARCHAR(100) NOT NULL,
  topping_price DECIMAL(12, 2) NOT NULL,
  PRIMARY KEY (order_item_id, topping_name),
  CONSTRAINT fk_oit_item FOREIGN KEY (order_item_id) REFERENCES order_items(id) ON DELETE CASCADE
);

CREATE TABLE reviews (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  order_id BIGINT NOT NULL,
  rating TINYINT NOT NULL,
  comment VARCHAR(1000) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_reviews_product FOREIGN KEY (product_id) REFERENCES products(id),
  CONSTRAINT fk_reviews_order FOREIGN KEY (order_id) REFERENCES orders(id),
  CONSTRAINT chk_rating CHECK (rating BETWEEN 1 AND 5),
  UNIQUE KEY uk_review (user_id, order_id, product_id)
);
