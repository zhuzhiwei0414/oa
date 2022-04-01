package cn.zzw.server.controller;


import cn.zzw.server.pojo.*;
import cn.zzw.server.service.IAdminService;
import cn.zzw.server.service.IMenuRoleService;

import cn.zzw.server.service.IRoleService;
import cn.zzw.server.utils.PageUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author zzw
 * @since 2022-01-17
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    @Resource
    private IRoleService roleService;
    @Resource
    private IAdminService adminService;
    @Resource
    private IMenuRoleService menuRoleService;

    @ApiOperation(value = "获取所有角色信息")
    @GetMapping("/roles")
    public List<Role> getAllRoles(){
        return roleService.list();
    }

    @ApiOperation(value = "通过操作员id查询其所拥有的角色权限")
    @GetMapping("/getAdminRoleList/{adminId}")
    public AdminRoleVo assignRole(@PathVariable Integer adminId){
        return roleService.getAdminRoleList(adminId);
    }

    @ApiOperation(value = "为用户分配角色")
    @PutMapping("/assignRole")
    public RespBean assignRole(@RequestBody AdminRoleVo adminRole){
        return adminService.addAdminRole(adminRole.getAdminId(),adminRole.getRid());
    }

    /**
     * 查询t_role角色表的分页列表信息
     * @param currPageNo 当前页码
     * @param pageSize 每页显示的数据行数
     * @param roleName 角色的英文和中文名称(查询条件)
     */
    @ApiOperation("查询t_role角色表的分页列表信息")
    @GetMapping("/pageRoleList")
    public RespBean pageRoleList(
            @RequestParam(value ="currPageNo", defaultValue = "1")Integer currPageNo,
            @RequestParam(value ="pageSize", defaultValue = "4")Integer pageSize,
            @RequestParam(value = "roleName", required = false) String roleName,
            @RequestParam(value = "sort")String sort) {

        Page<Role> page = new Page<Role>(currPageNo, pageSize);
        IPage<Role> adminIPage = roleService.selectByPage(page, roleName, sort);
        // 使用Math.toIntExact将Long类型转换成int类型
        Integer total = Math.toIntExact(adminIPage.getTotal());
        Integer current = Math.toIntExact(adminIPage.getCurrent());
        PageUtil<Role> pageList = new PageUtil<Role>(total,current,adminIPage.getRecords());
        return RespBean.success("获取角色列表列表成功", pageList);
    }

    @ApiOperation(value = "删除角色菜单权限")
    @DeleteMapping("/delMenuByRoleId/{roleId}/{menuId}")
    public RespBean delMenuByRoleId(@PathVariable Integer roleId,@PathVariable Integer menuId){
        List<Menu> menus=roleService.delMenuByRoleId(roleId, menuId);
        return  RespBean.success("权限删除成功！",menus);
    }

    @ApiOperation(value = "添加角色信息")
    @PostMapping("/saveRole")
    public RespBean addRole(@RequestBody Role role){
        if(!role.getName().startsWith("ROLE_")){
            role.setName("ROLE_"+role.getName());
        }
        if (roleService.save(role)){
            return RespBean.success("添加角色信息成功！");
        }
        return RespBean.error("添加角色信息失败");
    }

    @ApiOperation(value = "更新角色信息")
    @PutMapping("/updateRole")
    public RespBean updateRole(@RequestBody Role role){
        if(!role.getName().startsWith("ROLE_")){
            role.setName("ROLE_"+role.getName());
        }
        if(roleService.updateById(role)){
            return RespBean.success("更新角色信息成功！");
        }
        return RespBean.error("更新角色信息失败");
    }

    @ApiOperation(value = "删除角色信息")
    @DeleteMapping("/delRole/{rid}")
    public RespBean deleteRole(@PathVariable Integer rid){
        if(roleService.delRoleById(rid)){
            return RespBean.success("删除角色信息成功！");
        }
        return RespBean.error("删除角色信息失败！");
    }

    @ApiOperation("根据角色ID查询角色信息")
    @GetMapping("/getById/{roleId}")
    public Role getRole(@PathVariable Integer roleId){
        return roleService.getById(roleId);
    }

    @ApiOperation(value = "更新角色菜单")
    @PutMapping("/assignMenu")
    public RespBean updateMenuRole(@RequestBody MenuRoleVo menuRole){
        return menuRoleService.updateMenuRole(menuRole);
    }

}
