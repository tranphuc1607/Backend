-- ============================================================
-- DỮ LIỆU MẪU - Drink Order App
-- ============================================================
-- HƯỚNG DẪN SỬ DỤNG:
--   1. Khởi động Spring Boot để Hibernate tạo bảng tự động
--   2. Mở MySQL Workbench (hoặc DBeaver)
--   3. Chọn database: USE drink_order_db;
--   4. Chạy toàn bộ file này
-- ============================================================

USE drink_order_db;

-- ============================================================
-- 1. CATEGORIES - Danh mục đồ uống
-- ============================================================
INSERT INTO categories (name, description, image_url, active, created_at, updated_at) VALUES
('Trà sữa',   'Các loại trà sữa thơm ngon',      'https://i.imgur.com/tra-sua.jpg',   TRUE, NOW(), NOW()),
('Cà phê',    'Cà phê Việt và espresso',           'https://i.imgur.com/ca-phe.jpg',    TRUE, NOW(), NOW()),
('Nước ép',   'Nước ép trái cây tươi nguyên chất','https://i.imgur.com/nuoc-ep.jpg',   TRUE, NOW(), NOW()),
('Sinh tố',   'Sinh tố thơm ngon bổ dưỡng',       'https://i.imgur.com/sinh-to.jpg',   TRUE, NOW(), NOW()),
('Trà trái cây','Trà kết hợp hương vị trái cây',  'https://i.imgur.com/tra-traicay.jpg',TRUE, NOW(), NOW());

-- ============================================================
-- 2. TOPPINGS - Topping tùy chọn
-- ============================================================
INSERT INTO toppings (name, price, active, created_at, updated_at) VALUES
('Trân châu đen',  5000, TRUE, NOW(), NOW()),
('Trân châu trắng',5000, TRUE, NOW(), NOW()),
('Thạch dừa',      5000, TRUE, NOW(), NOW()),
('Thạch cà phê',   5000, TRUE, NOW(), NOW()),
('Kem cheese',     8000, TRUE, NOW(), NOW()),
('Pudding trứng',  8000, TRUE, NOW(), NOW()),
('Thạch matcha',   5000, TRUE, NOW(), NOW());

-- ============================================================
-- 3. PRODUCTS - Sản phẩm
-- ============================================================
INSERT INTO products (category_id, name, description, image_url, active, created_at, updated_at) VALUES
-- Trà sữa (category_id = 1)
(1, 'Trà sữa trân châu',      'Trà sữa Hong Kong kết hợp trân châu dai ngon', 'https://i.imgur.com/trasua1.jpg', TRUE, NOW(), NOW()),
(1, 'Trà sữa matcha',         'Matcha Nhật Bản hòa quyện cùng sữa tươi',      'https://i.imgur.com/trasua2.jpg', TRUE, NOW(), NOW()),
(1, 'Trà sữa oolong',         'Trà oolong thơm ngát pha cùng sữa béo',         'https://i.imgur.com/trasua3.jpg', TRUE, NOW(), NOW()),
-- Cà phê (category_id = 2)
(2, 'Cà phê sữa đá',          'Cà phê phin truyền thống, đậm đà, thơm ngon',  'https://i.imgur.com/caphe1.jpg',  TRUE, NOW(), NOW()),
(2, 'Bạc xỉu',                'Cà phê pha thêm nhiều sữa, vị nhẹ nhàng',      'https://i.imgur.com/caphe2.jpg',  TRUE, NOW(), NOW()),
(2, 'Cà phê đen đá',          'Cà phê đen nguyên chất, không đường',           'https://i.imgur.com/caphe3.jpg',  TRUE, NOW(), NOW()),
-- Nước ép (category_id = 3)
(3, 'Nước ép cam',            'Cam tươi nguyên chất, giàu vitamin C',          'https://i.imgur.com/nuocep1.jpg', TRUE, NOW(), NOW()),
(3, 'Nước ép dưa hấu',       'Dưa hấu mát lạnh ngày hè',                     'https://i.imgur.com/nuocep2.jpg', TRUE, NOW(), NOW()),
-- Sinh tố (category_id = 4)
(4, 'Sinh tố xoài',           'Xoài chín mọng, béo ngọt',                     'https://i.imgur.com/sinhto1.jpg', TRUE, NOW(), NOW()),
(4, 'Sinh tố bơ',             'Bơ sáp mịn, thơm ngậy',                        'https://i.imgur.com/sinhto2.jpg', TRUE, NOW(), NOW()),
-- Trà trái cây (category_id = 5)
(5, 'Trà đào cam sả',         'Trà thanh mát kết hợp đào, cam, sả',           'https://i.imgur.com/tra1.jpg',    TRUE, NOW(), NOW()),
(5, 'Trà vải nhãn',           'Trà hương vải nhãn ngọt dịu',                  'https://i.imgur.com/tra2.jpg',    TRUE, NOW(), NOW());

-- ============================================================
-- 4. PRODUCT_SIZES - Size của từng sản phẩm
-- ============================================================
-- Trà sữa trân châu (product_id = 1)
INSERT INTO product_sizes (product_id, size_name, price, created_at, updated_at) VALUES
(1, 'S', 25000, NOW(), NOW()),
(1, 'M', 30000, NOW(), NOW()),
(1, 'L', 35000, NOW(), NOW()),
-- Trà sữa matcha (product_id = 2)
(2, 'S', 30000, NOW(), NOW()),
(2, 'M', 35000, NOW(), NOW()),
(2, 'L', 40000, NOW(), NOW()),
-- Trà sữa oolong (product_id = 3)
(3, 'M', 28000, NOW(), NOW()),
(3, 'L', 33000, NOW(), NOW()),
-- Cà phê sữa đá (product_id = 4)
(4, 'M', 20000, NOW(), NOW()),
(4, 'L', 25000, NOW(), NOW()),
-- Bạc xỉu (product_id = 5)
(5, 'M', 22000, NOW(), NOW()),
(5, 'L', 27000, NOW(), NOW()),
-- Cà phê đen đá (product_id = 6)
(6, 'M', 18000, NOW(), NOW()),
(6, 'L', 22000, NOW(), NOW()),
-- Nước ép cam (product_id = 7)
(7, 'M', 30000, NOW(), NOW()),
(7, 'L', 38000, NOW(), NOW()),
-- Nước ép dưa hấu (product_id = 8)
(8, 'M', 28000, NOW(), NOW()),
(8, 'L', 35000, NOW(), NOW()),
-- Sinh tố xoài (product_id = 9)
(9, 'M', 35000, NOW(), NOW()),
(9, 'L', 45000, NOW(), NOW()),
-- Sinh tố bơ (product_id = 10)
(10, 'M', 38000, NOW(), NOW()),
(10, 'L', 48000, NOW(), NOW()),
-- Trà đào cam sả (product_id = 11)
(11, 'M', 35000, NOW(), NOW()),
(11, 'L', 42000, NOW(), NOW()),
-- Trà vải nhãn (product_id = 12)
(12, 'M', 32000, NOW(), NOW()),
(12, 'L', 38000, NOW(), NOW());

-- ============================================================
-- 5. PRODUCT_TOPPINGS - Topping áp dụng cho sản phẩm nào
-- ============================================================
-- Trà sữa trân châu: trân châu đen, trắng, thạch dừa, kem cheese
INSERT INTO product_toppings (product_id, topping_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 5),
-- Trà sữa matcha: trân châu đen, thạch matcha, kem cheese, pudding
(2, 1), (2, 5), (2, 6), (2, 7),
-- Trà sữa oolong: trân châu trắng, thạch dừa, kem cheese
(3, 2), (3, 3), (3, 5),
-- Cà phê sữa đá: thạch cà phê, kem cheese, pudding
(4, 4), (4, 5), (4, 6),
-- Bạc xỉu: kem cheese, pudding
(5, 5), (5, 6),
-- Cà phê đen: thạch cà phê
(6, 4),
-- Nước ép: không topping
-- Sinh tố xoài: thạch dừa, pudding
(9, 3), (9, 6),
-- Sinh tố bơ: thạch dừa
(10, 3),
-- Trà đào cam sả: trân châu trắng, thạch dừa
(11, 2), (11, 3),
-- Trà vải nhãn: trân châu trắng, thạch dừa
(12, 2), (12, 3);

-- ============================================================
-- KIỂM TRA KẾT QUẢ
-- ============================================================
-- Chạy các câu SELECT sau để xem dữ liệu đã insert:
/*
SELECT * FROM categories;
SELECT * FROM toppings;
SELECT p.id, p.name, c.name as category FROM products p JOIN categories c ON p.category_id = c.id;
SELECT ps.id, p.name, ps.size_name, ps.price FROM product_sizes ps JOIN products p ON ps.product_id = p.id;
SELECT p.name as product, t.name as topping FROM product_toppings pt
  JOIN products p ON pt.product_id = p.id
  JOIN toppings t ON pt.topping_id = t.id;
*/
