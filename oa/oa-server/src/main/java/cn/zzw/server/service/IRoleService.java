package cn.zzw.server.service;

import cn.zzw.server.pojo.AdminRoleVo;
import cn.zzw.server.pojo.Menu;
import cn.zzw.server.pojo.Role;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
public interface IRoleService extends IService<Role> {

    AdminRoleVo getAdminRoleList(Integer adminId);

    IPage<Role> selectByPage(Page<Role> page, String roleName, String sort);

    List<Menu> delMenuByRoleId(Integer roleId, Integer menuId);

    boolean delRoleById(Integer rid);
}
