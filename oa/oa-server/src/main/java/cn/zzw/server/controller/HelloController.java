package cn.zzw.server.controller;
/*
 *@Author AWei
 *@Create 2022/1/19-16:10
 *@Description 测试
 */

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }

//    @GetMapping("/employee/basic/list")
//    public String hello2(){
//        return "/employee/basic/hello";
//    }
//
//    @GetMapping("/employee/advanced/list")
//    public String hello3(){
//        return "/employee/advanced/hello";
//    }
}
