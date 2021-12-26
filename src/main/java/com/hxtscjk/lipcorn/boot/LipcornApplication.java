package com.hxtscjk.lipcorn.boot;

import love.forte.simbot.spring.autoconfigure.EnableSimbot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import tk.mybatis.spring.annotation.MapperScan;

@EnableSimbot
@SpringBootApplication
@ComponentScan(basePackages = {"com.hxtscjk.lipcorn.listen", "com.hxtscjk.lipcorn.service"})
@MapperScan("com.hxtscjk.lipcorn.mapper")
public class LipcornApplication {

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        return new ThreadPoolTaskScheduler();
    }

    public static void main(String[] args) {
        SpringApplication.run(LipcornApplication.class, args);

        System.out.println("小爆已启动~");

    }
}
