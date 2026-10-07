package com.ai;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * @author ：褚婧雯
 * @date ：2025/3/24 21:42
 * @description ：引导类程序
 */
@SpringBootApplication
@MapperScan("com.ai.mapper")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
