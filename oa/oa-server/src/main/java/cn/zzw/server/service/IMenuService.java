package cn.zzw.server.service;

import cn.zzw.server.pojo.Menu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author zzw
 * @since 2022-01-17
 */
public interface IMenuService extends IService<Menu> {

    List<Menu> getAllMenuList();

    /**
     * 根据用户id查询菜单列表
     * @return
     */
    List<Menu> getMenusByAdminId();

    /**
     * 根据角色获取菜单列表
     * @return
     */
    List<Menu> getMenusWithRole();

    List<Menu> getAllMenus();

    String getMenuIdsByRoleId(Integer roleId);

    boolean delMenuById(Integer id);

    boolean updateMenu(Menu menu);

    /**
     * 清除redis缓存
     */
    void clearMenuListFromRedis();
}
