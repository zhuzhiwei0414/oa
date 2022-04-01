package cn.zzw.server.controller;


import cn.zzw.server.pojo.RespBean;
import cn.zzw.server.pojo.Admin;
import cn.zzw.server.service.IAdminService;
import cn.zzw.server.utils.PageUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author zzw
 * @since 2022-01-17
 */
@Api(tags="用来操作管理员信息")
@RestController
@RequestMapping("/system/admin")
public class AdminController {

    @Resource
    private IAdminService adminService;
    @Resource
    private PasswordEncoder passwordEncoder;

    /**
     * 显示管理员分页列表信息
     * @param currPageNo 当前页码
     * @param pageSize 每页显示的数据行数
     * @param name 按用户名或手机号进行模糊查询
     */
//    @PreAuthorize("hasAuthority('/system/admin/list')")
    @ApiOperation("显示管理员分页列表信息")
    @GetMapping("/list")
    public RespBean getPageUserList(
            @RequestParam(value = "currPageNo", defaultValue = "1") Integer currPageNo,
            @RequestParam(value = "pageSize", defaultValue = "4")Integer pageSize,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "sort")String sort) {
        Page<Admin> page = new Page<>(currPageNo, pageSize);
        IPage<Admin> adminIPage = adminService.selectByPage(page, name,sort);
        // 使用Math.toIntExact将Long类型转换成int类型
        Integer total = Math.toIntExact(adminIPage.getTotal());
        Integer current = Math.toIntExact(adminIPage.getCurrent());
        PageUtil<Admin> pageList = new PageUtil(total,current,adminIPage.getRecords());
        return RespBean.success("获取操作员列表成功", pageList);
    }

    @ApiOperation(value = "通过id查询操作员信息")
    @GetMapping("/getById/{id}")
    public Admin getById(@PathVariable Integer id){
        return adminService.findById(id);
    }

    @ApiOperation(value = "修改操作员状态信息")
    @PutMapping("/updateState")
    public RespBean updateState(@RequestBody Admin admin){
        if (adminService.updateById(admin)){
            return RespBean.success("更新操作员状态信息成功！");
        }
        return RespBean.error("更新操作员状态信息失败！");
    }

    @ApiOperation(value = "修改操作员信息")
    @PutMapping("/updateAdmin")
    public RespBean updateAdmin(@RequestBody Admin admin){
        if (adminService.updateById(admin)){
            return RespBean.success("更新操作员信息成功！");
        }
        return RespBean.error("更新操作员信息失败！");
    }

    @ApiOperation(value = "删除操作员信息")
    @DeleteMapping("/delById/{id}")
    public RespBean deleteAdmin(@PathVariable Integer id){
        if (adminService.removeById(id)){
            return RespBean.success("删除操作员信息成功！");
        }
        return RespBean.error("删除操作员信息失败！");
    }

    @ApiOperation(value = "新增操作员信息")
    @PostMapping("/saveAdmin")
    public RespBean saveAdmin(@RequestBody Admin admin){
        // 对从前端传递过来的密码进行加密
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        // 再调用service里的saveAdmin方法实现保存新增的操作员信息的功能
        if(adminService.saveAdmin(admin)) {
            return RespBean.success("保存操作员信息成功");
        } else {
            return RespBean.error("保存操作员信息失败");
        }
    }



}
