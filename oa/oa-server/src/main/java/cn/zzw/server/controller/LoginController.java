package cn.zzw.server.controller;/*
 *@Author AWei
 *@Create 2022/1/19-9:32
 *@Description 登录
 */

import cn.zzw.server.pojo.Admin;
import cn.zzw.server.pojo.AdminLoginParam;
import cn.zzw.server.pojo.RespBean;
import cn.zzw.server.service.IAdminService;
import cn.zzw.server.service.IMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "LoginController")
@RestController
public class LoginController {
    @Resource
    private IAdminService adminService;
    @Resource
    private    IMenuService menuService;
    @ApiOperation(value = "登陆成功后返回token")
    @PostMapping("/login")
    public RespBean login(@RequestBody AdminLoginParam adminLoginParam, HttpServletRequest request){
        return adminService.login(adminLoginParam.getUsername(),adminLoginParam.getPassword(),
                adminLoginParam.getCode(),request);
    }

    @ApiOperation(value = "获取当前登录用户的信息")
    @GetMapping("/admin/info")
    public Map<String,Object> getAdminInfo(Principal principal){
        if(null==principal){
            principal=null;
        }
        String username=principal.getName();
        Admin admin=adminService.getAdminByUserName(username);
        admin.setPassword(null);
        //获取当前登录用户所拥有的角色列表
        admin.setRoles(adminService.getRoles(admin.getId()));
        // 查询当前登录用户所拥有的菜单URL集合(即所拥有的菜单权限，用于在前端进行按扭级别的权限控制)
        List<String> permissions = adminService.findMenuUrlByAdminId(admin.getId());
        Map<String, Object> map = new HashMap<String,Object>();
        map.put("admin", admin);
        map.put("permissions", permissions);
        return map;
    }

    @ApiOperation(value = "退出登录")
    @PostMapping("/logout")
    public RespBean logout(){
        menuService.clearMenuListFromRedis();
        return RespBean.success("注销成功！");
    }
}
