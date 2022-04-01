package cn.zzw.server.mapper;

import cn.zzw.server.pojo.Menu;
import cn.zzw.server.pojo.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.data.repository.query.Param;;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zzw
 * @since 2022-01-17
 */
public interface RoleMapper extends BaseMapper<Role> {

    List<Role> getRoles(Integer adminId);

    /**
     * 根据角色ID查询该角色所拥有的所有权限
     * @param roleId 角色ID
     * @return 返回指定角色所拥有的权限列表
     */
    List<Menu> findMenuByRole(@Param("roleId") Integer roleId);
}
