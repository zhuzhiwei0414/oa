package cn.zzw.server.mapper;

import cn.zzw.server.pojo.MenuRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zzw
 * @since 2022-01-17
 */
public interface MenuRoleMapper extends BaseMapper<MenuRole> {

    /**
     * 更新角色菜单
     * @param rid
     * @param mids
     * @return
     */
    Integer insertRecord(@Param("rid") Integer rid, @Param("mids") Integer[] mids);

    // 通过指定的菜单ID集合实现从t_menu_role菜单和角色中间表中删除当前菜单时将当前菜单下的子菜单列表一起删除
    public int deleteBatch(List<Integer> ids);
}
