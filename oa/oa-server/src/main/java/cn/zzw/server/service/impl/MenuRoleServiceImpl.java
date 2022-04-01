package cn.zzw.server.service.impl;

import cn.zzw.server.mapper.MenuRoleMapper;
import cn.zzw.server.pojo.MenuRole;
import cn.zzw.server.pojo.MenuRoleVo;
import cn.zzw.server.pojo.RespBean;
import cn.zzw.server.service.IMenuRoleService;
import cn.zzw.server.service.IMenuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zzw
 * @since 2022-01-17
 */
@Service
public class MenuRoleServiceImpl extends ServiceImpl<MenuRoleMapper, MenuRole> implements IMenuRoleService {

    @Resource
    private MenuRoleMapper menuRoleMapper;
    @Resource
    private IMenuService menuService;

    /**
     * 更新角色菜单
     * @param
     * @return
     */
    @Override
    @Transactional
    public RespBean updateMenuRole(MenuRoleVo menuRoleVo) {
        menuService.clearMenuListFromRedis();
        menuRoleMapper.delete(new QueryWrapper<MenuRole>().eq("rid",menuRoleVo.getRid()));
        if(null == menuRoleVo.getMid() || 0 == menuRoleVo.getMid().length){
            return RespBean.success("更新角色权限成功！");
        }
        int result=menuRoleMapper.insertRecord(menuRoleVo.getRid(),menuRoleVo.getMid());
        if (result==menuRoleVo.getMid().length){
            return RespBean.success("更新角色权限成功！");
        }
        return RespBean.success("更新角色权限失败！");
    }
}
