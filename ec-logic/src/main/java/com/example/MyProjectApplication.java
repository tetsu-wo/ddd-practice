package com.example; // あなたのプロジェクトのパッケージ名に合わせてください

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication
@MapperScan("com.example.infrastructure.db.mybatis.mapper") // MyBatisのマッパーをスキャン
public class MyProjectApplication {

    public static void main(String[] args) {
        // 2. この1行で、内部のWebサーバー（Tomcat）が一発で立ち上がります！
        SpringApplication.run(MyProjectApplication.class, args);
    }
}
