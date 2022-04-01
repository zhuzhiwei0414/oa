package cn.zzw.server.controller;/*
 *@Author AWei
 *@Create 2022/2/24-11:01
 *@Description 个人中心
 */

import cn.zzw.server.pojo.Admin;
import cn.zzw.server.pojo.RespBean;
import cn.zzw.server.service.IAdminService;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
public class AdminInfoController {

    @Resource
    private IAdminService adminService;

    @ApiOperation("更新当前用户信息")
    @PutMapping("/admin/info")
    public RespBean updateAdmin(@RequestBody Admin admin, Authentication authentication){
        if(adminService.updateById(admin)){
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                    admin,null, authentication.getAuthorities()));
            return RespBean.success("更新当前用户信息成功!");
        }
        return RespBean.error("更新当前用户信息失败");
    }

    @ApiOperation("更新当前用户密码")
    @PutMapping("/admin/pass")
    public RespBean updateAdminPass(@RequestBody Map<String,Object> info){
        String oldPass= (String) info.get("oldPass");
        String pass= (String) info.get("pass");
        Integer adminId= (Integer) info.get("adminId");
        return adminService.updateAdminPassword(oldPass,pass,adminId);
    }

//    @ApiOperation("更新用户头像")
//    @PostMapping("/admin/userFace")
//    public RespBean updateAdminUserFace(MultipartFile file,Integer id,Authentication authentication){
//        String[] filePath = FastDFSUtils.upload(file);
//        String url = FastDFSUtils.getTrackerUrl() + filePath[0] + "/" + filePath[1];
//        return adminService.updateAdminUserFace(url,id,authentication);
//    }

}
