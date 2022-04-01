package cn.zzw.server.controller;


import cn.zzw.server.pojo.Menu;
import cn.zzw.server.pojo.RespBean;
import cn.zzw.server.service.IMenuService;
import io.swagger.annotations.Api;
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
@Api(value = "用来操作菜单")
@RestController
@RequestMapping("/system/cfg")
public class MenuController {
    @Resource
    private IMenuService menuService;
    @ApiOperation(value = "通过用户ID查询菜单列表")
    @GetMapping(value = "/menu")
    public List<Menu> Menus(){
        return menuService.getMenusByAdminId();
    }

    @ApiOperation(value = "获取所有的菜单列表(非分页带层级关系用于绑定树形菜单控件)")
    @GetMapping("/getAllMenuList")
    public List<Menu> getAllMenuList() {
        return menuService.getAllMenuList();
    }

    @ApiOperation(value = "根据角色ID查询当前角色所拥有的所有权限id集合(格式以逗号分割的字符串)")
    @GetMapping("/getMenuIdsByRoleId/{roleId}")
    public String getMenuIdsByRoleId(@PathVariable Integer roleId){
        return menuService.getMenuIdsByRoleId(roleId);
    }

    @ApiOperation(value = "添加菜单信息")
    @PostMapping("/saveMenu")
    public RespBean saveMenu(@RequestBody Menu menu){
        if(menuService.save(menu)){
            return RespBean.success("保存菜单信息成功！");
        }
        return RespBean.error("保存菜单信息失败！");
    }

    @ApiOperation(value = "通过ID删除指定的菜单")
    @DeleteMapping("/delMenuById/{id}")
    public RespBean delMenuById(@PathVariable Integer id){
        if(menuService.delMenuById(id)){
            // 查询最新的菜单列表
            menuService.getMenusByAdminId();
            return RespBean.success("删除菜单信息成功！");
        }
        return RespBean.error("删除菜单信息失败！");
    }

    @ApiOperation(value = "通过id查询菜单信息")
    @GetMapping("/findByMenuId/{id}")
    public Menu findByMenuId(@PathVariable Integer id){
        return menuService.getById(id);
    }

    @ApiOperation(value = "通过id修改菜单信息")
    @PutMapping("/updateMenu")
    public RespBean updateMenu(@RequestBody Menu menu){
        if(menuService.updateMenu(menu)){
            // 查询最新的菜单列表
//            menuService.getMenusByAdminId();
            return RespBean.success("更新菜单信息成功！");
        }
        return RespBean.error("更新菜单信息失败！");
    }
}
