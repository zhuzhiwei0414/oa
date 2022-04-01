package cn.zzw.server.mapper;

import cn.zzw.server.pojo.Menu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zzw
 * @since 2022-01-17
 */
public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * @param id
     * @return 通过用户ID查询菜单列表
     */
    List<Menu> getMenusByAdminId(Integer id);

    /**
     * 根据角色获取菜单列表
     * @return
     */
    List<Menu> getMenusWithRole();

    List<Menu> getAllMenus();

    /**
     * 根据角色ID查询当前角色所拥有的所有权限id集合(格式以逗号分割的字符串)
     * @param roleId
     * @return
     */
    String getMenuIdsByRoleId(Integer roleId);
}
