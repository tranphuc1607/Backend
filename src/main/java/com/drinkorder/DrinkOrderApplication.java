package com.drinkorder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.drinkorder.config.AdminSeedProperties;
import com.drinkorder.config.JwtProperties;
import com.drinkorder.config.UploadProperties;

@SpringBootApplication
@EnableConfigurationProperties({
        JwtProperties.class,
        AdminSeedProperties.class,
        UploadProperties.class
})
public class DrinkOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(DrinkOrderApplication.class, args);
    }
}
