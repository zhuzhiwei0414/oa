package cn.zzw.server.mapper;

import cn.zzw.server.pojo.Admin;
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
public interface AdminMapper extends BaseMapper<Admin> {

    // 通过操作员用户ID查询其拥有菜单URL集合
    List<String> findMenuUrlByAdminId(Integer adminId);
}
