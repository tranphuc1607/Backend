package com.drinkorder.config;

import com.drinkorder.entity.Category;
import com.drinkorder.entity.Product;
import com.drinkorder.entity.ProductSize;
import com.drinkorder.entity.Topping;
import com.drinkorder.repository.CategoryRepository;
import com.drinkorder.repository.ProductRepository;
import com.drinkorder.repository.ToppingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Tạo sẵn thực đơn demo khi chạy trên database rỗng: 6 danh mục, 8 topping,
 * 24 món kèm size và topping.
 *
 * Nhờ vậy người mới clone repo về chỉ cần `docker compose up -d --build` là có
 * ngay thực đơn đầy đủ, không phải nhập tay hay chạy file SQL nào.
 *
 * Ảnh món nằm trong src/main/resources/static/seed-images nên đi theo repo và
 * nằm luôn trong jar. Spring Boot tự phục vụ thư mục static, không cần cấu hình.
 * Ảnh do admin tự upload thì ghi vào volume upload-data, tách biệt hoàn toàn.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeeder {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ToppingRepository toppingRepository;

    /** Ảnh seed phục vụ từ classpath, khác /uploads của ảnh admin tải lên. */
    private static final String IMG = "/seed-images/";

    @Bean
    CommandLineRunner seedDemoData() {
        return args -> {
            // Đã có dữ liệu thì không đụng vào, kể cả khi khởi động lại nhiều lần
            if (categoryRepository.count() > 0) {
                return;
            }

            // ===== Danh mục =====
            Category catTraSua = categoryRepository.save(Category.builder()
                    .name("Trà sữa")
                    .description("Các loại trà sữa")
                    .active(true)
                    .build());
            Category catCaPhe = categoryRepository.save(Category.builder()
                    .name("Cà phê")
                    .description("Cà phê Việt")
                    .active(true)
                    .build());
            Category catTraTraiCay = categoryRepository.save(Category.builder()
                    .name("Trà trái cây")
                    .description("Trà tươi pha cùng trái cây")
                    .active(true)
                    .build());
            Category catNuocEp = categoryRepository.save(Category.builder()
                    .name("Nước ép")
                    .description("Nước ép nguyên chất, không thêm đường")
                    .active(true)
                    .build());
            Category catDaXay = categoryRepository.save(Category.builder()
                    .name("Đá xay")
                    .description("Đồ uống đá xay mát lạnh, phủ kem")
                    .active(true)
                    .build());
            Category catSoda = categoryRepository.save(Category.builder()
                    .name("Soda")
                    .description("Soda ga mát, vị trái cây")
                    .active(true)
                    .build());

            // ===== Topping =====
            Topping topTranChauDen = toppingRepository.save(Topping.builder()
                    .name("Trân châu đen")
                    .price(new BigDecimal("5000"))
                    .active(true)
                    .build());
            Topping topKemCheese = toppingRepository.save(Topping.builder()
                    .name("Kem cheese")
                    .price(new BigDecimal("8000"))
                    .active(true)
                    .build());
            Topping topTranChauTrang = toppingRepository.save(Topping.builder()
                    .name("Trân châu trắng")
                    .price(new BigDecimal("6000"))
                    .active(true)
                    .build());
            Topping topThachDua = toppingRepository.save(Topping.builder()
                    .name("Thạch dừa")
                    .price(new BigDecimal("5000"))
                    .active(true)
                    .build());
            Topping topPuddingTrung = toppingRepository.save(Topping.builder()
                    .name("Pudding trứng")
                    .price(new BigDecimal("8000"))
                    .active(true)
                    .build());
            Topping topThachCaPhe = toppingRepository.save(Topping.builder()
                    .name("Thạch cà phê")
                    .price(new BigDecimal("6000"))
                    .active(true)
                    .build());
            Topping topDaoMieng = toppingRepository.save(Topping.builder()
                    .name("Đào miếng")
                    .price(new BigDecimal("10000"))
                    .active(true)
                    .build());
            Topping topKemBeo = toppingRepository.save(Topping.builder()
                    .name("Kem béo")
                    .price(new BigDecimal("7000"))
                    .active(true)
                    .build());

            // ----- Trà sữa -----
            Product p1 = Product.builder()
                    .category(catTraSua)
                    .name("Trà sữa trân châu")
                    .description("Trà sữa Hong Kong")
                    .imageUrl(IMG + "tra-sua-tran-chau.jpg")
                    .active(true)
                    .build();
            p1.getSizes().add(ProductSize.builder().product(p1)
                    .sizeName("S").price(new BigDecimal("25000")).build());
            p1.getSizes().add(ProductSize.builder().product(p1)
                    .sizeName("M").price(new BigDecimal("30000")).build());
            p1.getSizes().add(ProductSize.builder().product(p1)
                    .sizeName("L").price(new BigDecimal("35000")).build());
            p1.setToppings(Set.of(topTranChauDen, topKemCheese));
            productRepository.save(p1);

            Product p2 = Product.builder()
                    .category(catTraSua)
                    .name("Trà sữa matcha")
                    .description("Matcha Nhật đánh cùng sữa tươi")
                    .imageUrl(IMG + "tra-sua-matcha.jpg")
                    .active(true)
                    .build();
            p2.getSizes().add(ProductSize.builder().product(p2)
                    .sizeName("M").price(new BigDecimal("35000")).build());
            p2.getSizes().add(ProductSize.builder().product(p2)
                    .sizeName("L").price(new BigDecimal("42000")).build());
            p2.setToppings(Set.of(topTranChauDen, topKemCheese, topTranChauTrang, topThachDua, topPuddingTrung));
            productRepository.save(p2);

            Product p3 = Product.builder()
                    .category(catTraSua)
                    .name("Trà sữa khoai môn")
                    .description("Khoai môn nghiền, vị bùi ngọt dịu")
                    .imageUrl(IMG + "tra-sua-khoai-mon.jpg")
                    .active(true)
                    .build();
            p3.getSizes().add(ProductSize.builder().product(p3)
                    .sizeName("M").price(new BigDecimal("33000")).build());
            p3.getSizes().add(ProductSize.builder().product(p3)
                    .sizeName("L").price(new BigDecimal("40000")).build());
            p3.setToppings(Set.of(topTranChauDen, topKemCheese, topTranChauTrang, topThachDua, topPuddingTrung));
            productRepository.save(p3);

            Product p4 = Product.builder()
                    .category(catTraSua)
                    .name("Trà sữa socola")
                    .description("Socola đậm pha sữa tươi")
                    .imageUrl(IMG + "tra-sua-socola.jpg")
                    .active(true)
                    .build();
            p4.getSizes().add(ProductSize.builder().product(p4)
                    .sizeName("M").price(new BigDecimal("34000")).build());
            p4.getSizes().add(ProductSize.builder().product(p4)
                    .sizeName("L").price(new BigDecimal("41000")).build());
            p4.setToppings(Set.of(topTranChauDen, topKemCheese, topTranChauTrang, topThachDua, topPuddingTrung));
            productRepository.save(p4);

            Product p5 = Product.builder()
                    .category(catTraSua)
                    .name("Hồng trà sữa")
                    .description("Hồng trà ủ lạnh, hậu vị chát nhẹ")
                    .imageUrl(IMG + "hong-tra-sua.jpg")
                    .active(true)
                    .build();
            p5.getSizes().add(ProductSize.builder().product(p5)
                    .sizeName("M").price(new BigDecimal("30000")).build());
            p5.getSizes().add(ProductSize.builder().product(p5)
                    .sizeName("L").price(new BigDecimal("37000")).build());
            p5.setToppings(Set.of(topTranChauDen, topKemCheese, topTranChauTrang, topThachDua, topPuddingTrung));
            productRepository.save(p5);

            Product p6 = Product.builder()
                    .category(catTraSua)
                    .name("Trà sữa oolong nướng")
                    .description("Oolong nướng thơm, ít ngọt")
                    .imageUrl(IMG + "tra-sua-oolong-nuong.jpg")
                    .active(true)
                    .build();
            p6.getSizes().add(ProductSize.builder().product(p6)
                    .sizeName("M").price(new BigDecimal("36000")).build());
            p6.getSizes().add(ProductSize.builder().product(p6)
                    .sizeName("L").price(new BigDecimal("43000")).build());
            p6.setToppings(Set.of(topTranChauDen, topKemCheese, topTranChauTrang, topThachDua, topPuddingTrung));
            productRepository.save(p6);

            // ----- Cà phê -----
            Product p7 = Product.builder()
                    .category(catCaPhe)
                    .name("Cà phê sữa đá")
                    .description("Cà phê phin")
                    .imageUrl(IMG + "ca-phe-sua-da.jpg")
                    .active(true)
                    .build();
            p7.getSizes().add(ProductSize.builder().product(p7)
                    .sizeName("M").price(new BigDecimal("20000")).build());
            p7.getSizes().add(ProductSize.builder().product(p7)
                    .sizeName("L").price(new BigDecimal("25000")).build());
            productRepository.save(p7);

            Product p8 = Product.builder()
                    .category(catCaPhe)
                    .name("Cà phê đen đá")
                    .description("Cà phê phin truyền thống, không sữa")
                    .imageUrl(IMG + "ca-phe-den-da.jpg")
                    .active(true)
                    .build();
            p8.getSizes().add(ProductSize.builder().product(p8)
                    .sizeName("M").price(new BigDecimal("18000")).build());
            p8.getSizes().add(ProductSize.builder().product(p8)
                    .sizeName("L").price(new BigDecimal("22000")).build());
            p8.setToppings(Set.of(topKemCheese, topThachCaPhe, topKemBeo));
            productRepository.save(p8);

            Product p9 = Product.builder()
                    .category(catCaPhe)
                    .name("Bạc xỉu")
                    .description("Nhiều sữa, cà phê nhẹ")
                    .imageUrl(IMG + "bac-xiu.jpg")
                    .active(true)
                    .build();
            p9.getSizes().add(ProductSize.builder().product(p9)
                    .sizeName("M").price(new BigDecimal("25000")).build());
            p9.getSizes().add(ProductSize.builder().product(p9)
                    .sizeName("L").price(new BigDecimal("30000")).build());
            p9.setToppings(Set.of(topKemCheese, topThachCaPhe, topKemBeo));
            productRepository.save(p9);

            Product p10 = Product.builder()
                    .category(catCaPhe)
                    .name("Cà phê muối")
                    .description("Lớp kem muối mặn ngọt phủ trên")
                    .imageUrl(IMG + "ca-phe-muoi.jpg")
                    .active(true)
                    .build();
            p10.getSizes().add(ProductSize.builder().product(p10)
                    .sizeName("M").price(new BigDecimal("29000")).build());
            p10.getSizes().add(ProductSize.builder().product(p10)
                    .sizeName("L").price(new BigDecimal("35000")).build());
            p10.setToppings(Set.of(topKemCheese, topThachCaPhe, topKemBeo));
            productRepository.save(p10);

            Product p11 = Product.builder()
                    .category(catCaPhe)
                    .name("Cappuccino")
                    .description("Espresso cùng lớp bọt sữa dày")
                    .imageUrl(IMG + "cappuccino.jpg")
                    .active(true)
                    .build();
            p11.getSizes().add(ProductSize.builder().product(p11)
                    .sizeName("M").price(new BigDecimal("39000")).build());
            p11.getSizes().add(ProductSize.builder().product(p11)
                    .sizeName("L").price(new BigDecimal("45000")).build());
            p11.setToppings(Set.of(topKemCheese, topThachCaPhe, topKemBeo));
            productRepository.save(p11);

            Product p12 = Product.builder()
                    .category(catCaPhe)
                    .name("Latte")
                    .description("Espresso pha sữa tươi, vị êm")
                    .imageUrl(IMG + "latte.jpg")
                    .active(true)
                    .build();
            p12.getSizes().add(ProductSize.builder().product(p12)
                    .sizeName("M").price(new BigDecimal("39000")).build());
            p12.getSizes().add(ProductSize.builder().product(p12)
                    .sizeName("L").price(new BigDecimal("45000")).build());
            p12.setToppings(Set.of(topKemCheese, topThachCaPhe, topKemBeo));
            productRepository.save(p12);

            // ----- Trà trái cây -----
            Product p13 = Product.builder()
                    .category(catTraTraiCay)
                    .name("Trà đào cam sả")
                    .description("Đào miếng, cam tươi và sả thơm")
                    .imageUrl(IMG + "tra-dao-cam-sa.jpg")
                    .active(true)
                    .build();
            p13.getSizes().add(ProductSize.builder().product(p13)
                    .sizeName("M").price(new BigDecimal("39000")).build());
            p13.getSizes().add(ProductSize.builder().product(p13)
                    .sizeName("L").price(new BigDecimal("45000")).build());
            p13.setToppings(Set.of(topTranChauTrang, topThachDua, topDaoMieng));
            productRepository.save(p13);

            Product p14 = Product.builder()
                    .category(catTraTraiCay)
                    .name("Trà vải")
                    .description("Vải thiều ngâm, trà xanh")
                    .imageUrl(IMG + "tra-vai.jpg")
                    .active(true)
                    .build();
            p14.getSizes().add(ProductSize.builder().product(p14)
                    .sizeName("M").price(new BigDecimal("37000")).build());
            p14.getSizes().add(ProductSize.builder().product(p14)
                    .sizeName("L").price(new BigDecimal("43000")).build());
            p14.setToppings(Set.of(topTranChauTrang, topThachDua, topDaoMieng));
            productRepository.save(p14);

            Product p15 = Product.builder()
                    .category(catTraTraiCay)
                    .name("Trà chanh dây")
                    .description("Chanh dây tươi, vị chua thanh")
                    .imageUrl(IMG + "tra-chanh-day.jpg")
                    .active(true)
                    .build();
            p15.getSizes().add(ProductSize.builder().product(p15)
                    .sizeName("M").price(new BigDecimal("35000")).build());
            p15.getSizes().add(ProductSize.builder().product(p15)
                    .sizeName("L").price(new BigDecimal("41000")).build());
            p15.setToppings(Set.of(topTranChauTrang, topThachDua, topDaoMieng));
            productRepository.save(p15);

            Product p16 = Product.builder()
                    .category(catTraTraiCay)
                    .name("Trà ổi hồng")
                    .description("Ổi hồng xay, thêm chút muối ớt")
                    .imageUrl(IMG + "tra-oi-hong.jpg")
                    .active(true)
                    .build();
            p16.getSizes().add(ProductSize.builder().product(p16)
                    .sizeName("M").price(new BigDecimal("38000")).build());
            p16.getSizes().add(ProductSize.builder().product(p16)
                    .sizeName("L").price(new BigDecimal("44000")).build());
            p16.setToppings(Set.of(topTranChauTrang, topThachDua, topDaoMieng));
            productRepository.save(p16);

            // ----- Nước ép -----
            Product p17 = Product.builder()
                    .category(catNuocEp)
                    .name("Nước ép cam")
                    .description("Cam vắt nguyên chất")
                    .imageUrl(IMG + "nuoc-ep-cam.jpg")
                    .active(true)
                    .build();
            p17.getSizes().add(ProductSize.builder().product(p17)
                    .sizeName("M").price(new BigDecimal("35000")).build());
            p17.getSizes().add(ProductSize.builder().product(p17)
                    .sizeName("L").price(new BigDecimal("42000")).build());
            productRepository.save(p17);

            Product p18 = Product.builder()
                    .category(catNuocEp)
                    .name("Nước ép dưa hấu")
                    .description("Dưa hấu đỏ ép lạnh")
                    .imageUrl(IMG + "nuoc-ep-dua-hau.jpg")
                    .active(true)
                    .build();
            p18.getSizes().add(ProductSize.builder().product(p18)
                    .sizeName("M").price(new BigDecimal("32000")).build());
            p18.getSizes().add(ProductSize.builder().product(p18)
                    .sizeName("L").price(new BigDecimal("38000")).build());
            productRepository.save(p18);

            Product p19 = Product.builder()
                    .category(catNuocEp)
                    .name("Nước ép cà rốt táo")
                    .description("Cà rốt và táo, nhiều vitamin")
                    .imageUrl(IMG + "nuoc-ep-ca-rot-tao.jpg")
                    .active(true)
                    .build();
            p19.getSizes().add(ProductSize.builder().product(p19)
                    .sizeName("M").price(new BigDecimal("36000")).build());
            p19.getSizes().add(ProductSize.builder().product(p19)
                    .sizeName("L").price(new BigDecimal("43000")).build());
            productRepository.save(p19);

            // ----- Đá xay -----
            Product p20 = Product.builder()
                    .category(catDaXay)
                    .name("Frappe socola")
                    .description("Socola đá xay, phủ kem tươi")
                    .imageUrl(IMG + "frappe-socola.jpg")
                    .active(true)
                    .build();
            p20.getSizes().add(ProductSize.builder().product(p20)
                    .sizeName("M").price(new BigDecimal("45000")).build());
            p20.getSizes().add(ProductSize.builder().product(p20)
                    .sizeName("L").price(new BigDecimal("52000")).build());
            p20.setToppings(Set.of(topKemCheese, topKemBeo));
            productRepository.save(p20);

            Product p21 = Product.builder()
                    .category(catDaXay)
                    .name("Frappe matcha")
                    .description("Matcha đá xay, ngọt dịu")
                    .imageUrl(IMG + "frappe-matcha.jpg")
                    .active(true)
                    .build();
            p21.getSizes().add(ProductSize.builder().product(p21)
                    .sizeName("M").price(new BigDecimal("47000")).build());
            p21.getSizes().add(ProductSize.builder().product(p21)
                    .sizeName("L").price(new BigDecimal("54000")).build());
            p21.setToppings(Set.of(topKemCheese, topKemBeo));
            productRepository.save(p21);

            Product p22 = Product.builder()
                    .category(catDaXay)
                    .name("Cookie đá xay")
                    .description("Bánh cookie nghiền cùng sữa")
                    .imageUrl(IMG + "cookie-da-xay.jpg")
                    .active(true)
                    .build();
            p22.getSizes().add(ProductSize.builder().product(p22)
                    .sizeName("M").price(new BigDecimal("49000")).build());
            p22.getSizes().add(ProductSize.builder().product(p22)
                    .sizeName("L").price(new BigDecimal("56000")).build());
            p22.setToppings(Set.of(topKemCheese, topKemBeo));
            productRepository.save(p22);

            // ----- Soda -----
            Product p23 = Product.builder()
                    .category(catSoda)
                    .name("Soda việt quất")
                    .description("Việt quất và soda ga")
                    .imageUrl(IMG + "soda-viet-quat.jpg")
                    .active(true)
                    .build();
            p23.getSizes().add(ProductSize.builder().product(p23)
                    .sizeName("M").price(new BigDecimal("33000")).build());
            p23.getSizes().add(ProductSize.builder().product(p23)
                    .sizeName("L").price(new BigDecimal("39000")).build());
            p23.setToppings(Set.of(topThachDua, topDaoMieng));
            productRepository.save(p23);

            Product p24 = Product.builder()
                    .category(catSoda)
                    .name("Soda chanh bạc hà")
                    .description("Chanh tươi, lá bạc hà")
                    .imageUrl(IMG + "soda-chanh-bac-ha.jpg")
                    .active(true)
                    .build();
            p24.getSizes().add(ProductSize.builder().product(p24)
                    .sizeName("M").price(new BigDecimal("30000")).build());
            p24.getSizes().add(ProductSize.builder().product(p24)
                    .sizeName("L").price(new BigDecimal("36000")).build());
            p24.setToppings(Set.of(topThachDua, topDaoMieng));
            productRepository.save(p24);
            log.info("Đã tạo thực đơn demo: {} danh mục, {} món, {} topping",
                    categoryRepository.count(), productRepository.count(), toppingRepository.count());
        };
    }
}
