package cn.zzw.server;
/*
 *@Author AWei
 *@Create 2022/1/17-14:00
 *@Description
 */

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@MapperScan("cn.zzw.server.mapper")
@EnableGlobalMethodSecurity(securedEnabled = true,prePostEnabled = true)
public class  YebApplication {
    public static void main(String[] args) {
        SpringApplication.run(YebApplication.class,args);
    }
}
