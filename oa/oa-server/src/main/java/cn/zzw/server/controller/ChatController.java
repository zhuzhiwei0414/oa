package cn.zzw.server.controller;/*
 *@Author AWei
 *@Create 2022/2/24-9:19
 *@Description 在线聊天
 */

import cn.zzw.server.pojo.Admin;
import cn.zzw.server.service.IAdminService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Resource
    private IAdminService adminService;

//    @ApiOperation(value = "获取所有操作员信息")
//    @GetMapping("/admin")
//    public List<Admin> getAllAdmins(String keywords){
//        return adminService.getAllAdmins(keywords);
//    }

}
