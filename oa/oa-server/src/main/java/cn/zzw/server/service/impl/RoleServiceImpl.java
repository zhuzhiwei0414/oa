package cn.zzw.server.service.impl;

import cn.zzw.server.mapper.AdminRoleMapper;
import cn.zzw.server.mapper.MenuMapper;
import cn.zzw.server.mapper.MenuRoleMapper;
import cn.zzw.server.mapper.RoleMapper;
import cn.zzw.server.pojo.*;
import cn.zzw.server.service.IRoleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zzw
 * @since 2022-01-17
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    @Resource
    private AdminRoleMapper adminRoleMapper;
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private MenuRoleMapper menuRoleMapper;
    @Resource
    private MenuMapper menuMapper;

    /**
     *  为用户分配角色权限
     * @param adminId
     * @return
     */
    @Override
    public AdminRoleVo getAdminRoleList(Integer adminId) {
        List<AdminRole> list = adminRoleMapper.selectList(new QueryWrapper<AdminRole>().eq("adminId", adminId));
        //如果当前用户没有被分配一个角色权限,那么就返回空的AdminRoleVo对象
        if(list==null||list.size()==0){
            return new AdminRoleVo();
        }
        List<Integer> ids=new ArrayList<>();
        for (AdminRole admin:list
             ) {
            ids.add(admin.getRid());
        }
        return new AdminRoleVo(adminId,ids.toArray(new Integer[ids.size()]));
    }

    /**
     *  分页查询角色列表
     * @param page
     * @param query
     * @param sort
     * @return
     */
    @Override
    public IPage<Role> selectByPage(Page<Role> page, String query, String sort) {
        QueryWrapper<Role> queryWrapper=new QueryWrapper<>();
        if(StringUtils.isNotEmpty(query)){
            // 如果查询条件query不为空,那么就按角色英文名称或角色中文名称模糊查询
            queryWrapper.like("name",query).or().like("nameZh",query);
        }
        //按id字段进行升序或降序拍(取决前段点击ID所在列头)
        if(sort.equals("+id")){
            queryWrapper.orderByAsc("id");
        }else{
            queryWrapper.orderByDesc("id");
        }
        IPage<Role> rolePage = roleMapper.selectPage(page, queryWrapper);
        // 获取到角色列表
        List<Role> roleList = rolePage.getRecords();
        //遍历所有的角色列表信息
        roleList.stream().map(role -> {
            // 遍历所有的角色列表，查询每个角色下的权限列表(这个权限列表需要进行一级、二级、三级分类)
            role.setChildren(getRightListByRole(role));
            return role;
        }).collect(Collectors.toList());
        return rolePage;
    }

    /**
     *  删除角色权限
     * @param roleId
     * @param menuId
     * @return
     */
    @Override
    public List<Menu> delMenuByRoleId(Integer roleId, Integer menuId) {
        //查询menu权限对象的所有子权限，并将这些子权限的id追加到List集合中
        List<Integer> sonMenuIds=getSonMenuIds(roleId,menuId);
        Integer [] menuIds=null;//多个子权限id使用逗号进行拼接
        if(sonMenuIds!=null&&sonMenuIds.size()>0){
            //将List<Integer>转换成Integer[]数据
            menuIds=sonMenuIds.toArray(new Integer[sonMenuIds.size()]);
        }
        // 1、根据角色ID删除对应的权限(从t_menu_role表)
        QueryWrapper<MenuRole> wrapper = new QueryWrapper<MenuRole>();
        wrapper.eq("rid", roleId);
        if(menuIds==null||menuIds.length==0){
            //说明本次删除时删除角色下的三级权限
            wrapper.eq("mid", menuId);
        }else{
            // 说明本次删除是删除角色下的一级或二级权限
            // DELETE FROM `t_menu_role` WHERE rid = 1 AND `mid` IN (10, 46)
            wrapper.in("mid", menuIds);
        }
        // 会自动生成sql语句: delete from t_menu_role where mid = ? and rid = ?
        menuRoleMapper.delete(wrapper);
        // 2、将当前角色最新的权限列表查询出来，再进行返回
        Role role = new Role();
        role.setId(roleId);
        List<Menu> menus = getRightListByRole(role);
        return menus;
    }

    /**
     * 删除角色相关信息
     * @param rid
     * @return
     */
    @Override
    public boolean delRoleById(Integer rid) {
        try {
            //通过角色id删除用户相关信息
            adminRoleMapper.delete(new QueryWrapper<AdminRole>().eq("rid",rid));
            //通过角色ID删除菜单相关信息
            menuRoleMapper.delete(new QueryWrapper<MenuRole>().eq("rid",rid));
            //通过角色ID删除角色相关信息
            roleMapper.deleteById(rid);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 查询menu权限对象的所有子权限，并将这些子权限的id追加到List集合中
    private List<Integer> getSonMenuIds(Integer roleId, Integer menuId) {
        List<Integer> menuIds=new ArrayList<>();
        //通过即将要删除的权限ID查询其详情对象信息
        Menu delMenu = menuMapper.selectById(menuId);
        if(delMenu.getMenuLevel()!=3){
            //判断要删除的菜单的等级是否为3，及二级权限，如果不是三级权限，那么再删除比如二级权限时，
            //后面还需要将二级权限下的子权限一起从t_menu_role表中删除
            Role role=new Role();
            role.setId(roleId);
            //获取当前角色下的所有权限
            List<Menu> menus = getRightListByRole(role);
            //判断即将要删除的权限其等级是1还是2
            if(delMenu.getMenuLevel()==1){
                //一级权限
                for (Menu currMenu:menus
                     ) {
                    if(currMenu.getId()==delMenu.getId()){
                        menuIds.add(currMenu.getId());
                        //遍历一级下的二级权限
                        for (Menu sonMenu:currMenu.getChildren()
                             ) {
                            menuIds.add(sonMenu.getId());
                            //遍历二级权限下的三级权限
                            for (Menu threeMenu:sonMenu.getChildren()
                                 ) {
                                menuIds.add(threeMenu.getId());
                            }
                        }
                        return menuIds;
                    }
                }
            }else{
                //二级权限
                for (Menu currMenu:menus
                     ) {
                    //遍历二级权限
                    for (Menu twoMenu:currMenu.getChildren()
                         ) {
                        if(twoMenu.getId()==delMenu.getId()){
                           menuIds.add(twoMenu.getId());
                           //便利二级下的三级权限
                            for (Menu threeMenu:twoMenu.getChildren()
                                 ) {
                                menuIds.add(threeMenu.getId());
                            }
                            return menuIds;
                        }
                    }
                }
            }
            return null;
        }else{
            return null;
        }
    }

    //查询当前角色所授权的所有权限
    private List<Menu> getRightListByRole(Role role){
        //查询当前角色下的权限列表集合
        List<Menu> menuList = roleMapper.findMenuByRole(role.getId());
        List<Menu> oneLevel = null;
        if(menuList!=null&&menuList.size()>0){
            // 得到一级菜单权限
            oneLevel = menuList.stream().filter((menu) -> {
                // 过滤出当前角色下的一级权限列表，之后再调用map对这个一级权限列表进行遍历
                return menu.getParentId()!=null&& menu.getParentId()==1;
            }).collect(Collectors.toList());

            // 遍历有的一级菜单
            oneLevel = oneLevel.stream().map((oneLevelMenu) -> {
                //遍历一级权限得到一级权限下的二级权限
                oneLevelMenu.setChildren(getChildens(oneLevelMenu,menuList));
                return oneLevelMenu;
            }).collect(Collectors.toList());

            /*menuList.stream().filter((menu) -> {
                // 过滤出当前角色下的一级权限列表，之后再调用map对这个一级权限列表进行遍历
                 return menu.getParentId()!=null&& menu.getParentId()==1;
            }).map((oneLevelMenu) -> {
                //调用map对这个一级权限列表进行遍历
                //遍历一级权限得到一级权限下的二级权限
                oneLevelMenu.setChildren(getChildens(oneLevelMenu,menuList));
                return oneLevelMenu;
            }).collect(Collectors.toList());*/
        }else{
            return null;
        }
        return oneLevel;
    }


    /**
     * 得到当前权限下的子权限集合
     * @param oneLevelMenu 权限对象(比如一级或二级)
     * @param menuList 为当前角色所拥有的所有权限集合
     * @return 返回当前权限下的子权限列表
     */
    private List<Menu> getChildens(Menu oneLevelMenu, List<Menu> menuList) {
        List<Menu> childMenuList = menuList.stream().filter((menu) ->
            //过滤条件是：权限集合中menu对象的pid父级ID==当前权限对象oneLevelMenu的ID
            //实现将当前权限对象oneLevelMenu的子权限列表过滤出来，如果onLevelMenu为一级权限
            //那么就可以把这个把这个一级权限下的二级权限给过滤出来
            menu.getParentId() == oneLevelMenu.getId()
        ).map((sonMenu) -> {
            sonMenu.setChildren(getChildens(sonMenu,menuList));
            return sonMenu;
        }).collect(Collectors.toList());
        return childMenuList;
    }

}
