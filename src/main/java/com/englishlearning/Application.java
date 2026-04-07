package com.englishlearning;

import com.englishlearning.util.DataInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        
        // 初始化数据
        DataInitializer dataInitializer = context.getBean(DataInitializer.class);
        dataInitializer.initializeData();
    }

}