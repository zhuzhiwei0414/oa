package cn.zzw.server.service;

import cn.zzw.server.pojo.MenuRole;
import cn.zzw.server.pojo.MenuRoleVo;
import cn.zzw.server.pojo.RespBean;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author zzw
 * @since 2022-01-17
 */
public interface IMenuRoleService extends IService<MenuRole> {


    RespBean updateMenuRole(MenuRoleVo menuRoleVo);
}
