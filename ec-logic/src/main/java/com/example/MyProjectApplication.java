package com.example; // あなたのプロジェクトのパッケージ名に合わせてください

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
}) // 1. これが「Springの起動スイッチ」の目印です！
public class MyProjectApplication {

    public static void main(String[] args) {
        // 2. この1行で、内部のWebサーバー（Tomcat）が一発で立ち上がります！
        SpringApplication.run(MyProjectApplication.class, args);
    }
}
