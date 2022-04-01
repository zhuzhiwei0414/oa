package cn.zzw.server.service.impl;

import cn.zzw.server.mapper.MenuRoleMapper;
import cn.zzw.server.pojo.Admin;
import cn.zzw.server.utils.AdminUtils;
import cn.zzw.server.mapper.MenuMapper;
import cn.zzw.server.pojo.Menu;
import cn.zzw.server.service.IMenuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zzw
 * @since 2022-01-17
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {
    @Resource
    private MenuMapper menuMapper;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private MenuRoleMapper menuRoleMapper;

    /**
     * 获取所有的菜单列表（非分页带层级关系用于绑定树形菜单控件）
     * @return
     */
    @Override
    public List<Menu> getAllMenuList() {
        //1.获取所有的菜单列表
        List<Menu> menus = menuMapper.selectList(null);
        //2.对获取到的菜单列表进行分页，即查询出每层菜单的子菜单列表
        List<Menu> oneLevelMenus=getLevelMenuList(menus);
        return oneLevelMenus;
    }

    //查询出每层菜单的子菜单列表
    private List<Menu> getLevelMenuList(List<Menu> menuList) {
        //oneLevelMenus 为一级菜单
        List<Menu> oneLevelMenus=menuList.stream().filter((menu) -> {
            //过滤出当前角色下的一级权限列表，之后再调用map对这个一级权限列表进行遍历
            return menu.getParentId()!=null&&menu.getParentId()==1;
        }).map((oneLevelMenu)->{//调用map对这个一级权限进行遍历
            //遍历一级权限得到一级权限下的二级权限
            oneLevelMenu.setChildren(getChildrens(oneLevelMenu,menuList));
            return oneLevelMenu;
        }).collect(Collectors.toList());
        return oneLevelMenus;
    }

    /**
     * 得到当前权限下的子权限集合
     * @param oneLevelMenu 权限对象(比如一级或二级)
     * @param menuList 为当前角色所拥有的所有权限集合
     * @return 返回当前权限下的子权限列表
     */
    private List<Menu> getChildrens(Menu oneLevelMenu, List<Menu> menuList) {
        List<Menu> childMenuList=menuList.stream().filter((menu)->
            //过滤条件是：权限集合中menu对象的pid父级ID==当前权限对象oneLevelMenu的ID
            //实现将当前权限对象oneLevelMenu的子权限列表过滤出来，
            //如果oneLevelMenu为一级权限，那么就可以把这个一级权限下的二级权限给过滤出来。
            menu.getParentId()==oneLevelMenu.getId()
        ).map((sonMenu)->{
            //调用map方法，将过滤出来的子权限查询进行遍历
            sonMenu.setChildren(getChildrens(sonMenu,menuList));
            return sonMenu;
        }).collect(Collectors.toList());
        return childMenuList;
    }

    /**
     *
     * @return 通过用户ID查询菜单列表
     */
    @Override
    public List<Menu> getMenusByAdminId() {
        Integer adminId= AdminUtils.getCurrentAdmin().getId();
        ValueOperations<String,Object> valueOperations=redisTemplate.opsForValue();
        //从Redis获取菜单数据
        List<Menu> menus=(List<Menu>)valueOperations.get("menu_"+adminId);
        //如果为空，就从数据库中获取
        if(CollectionUtils.isEmpty(menus)){
            menus=menuMapper.getMenusByAdminId(adminId);
            //将数据保存到redis中
            valueOperations.set("menu_"+adminId,menus);
        }
        return menus;
    }

    /**
     * 根据角色获取菜单列表
     * @return
     */
    @Override
    public List<Menu> getMenusWithRole() {
        return menuMapper.getMenusWithRole();
    }

    /**
     * 获取所有的菜单列表
     * @return
     */
    @Override
    public List<Menu> getAllMenus() {
        return menuMapper.getAllMenus();
    }

    /**
     * 通过角色id查询下面的菜单列表
     * @param roleId
     * @return
     */
    @Override
    public String getMenuIdsByRoleId(Integer roleId) {
        return menuMapper.getMenuIdsByRoleId(roleId);
    }


    //根据菜单id删除指定的菜单信息
    @Override
    public boolean delMenuById(Integer id) {
        //再删除指定的权限时需要到t_menu_role权限与角色中间表中删除跟这个菜单权限相关的数据
        //再到t_menu菜单权限中删除指定的菜单权限信息
        //1.通过菜单权限id到t_menu_role菜单与角色中间表中，
        //删除这个菜单权限相关的信息
        try {
            //1.创建list集合，用于封装所有删除菜单id值，
            //LinkedList是保证在调用addFirst()将id前置插入
            LinkedList<Integer> idsList=new LinkedList<>();
            //把当前id封装到list里面
            idsList.addFirst(id);
            //2.向idList集合设置删除菜单id
            this.selectChildMenuById(id,idsList);
            //2.通过菜单权限id到t_menu_role菜单与角色中间表中，删除这个菜单权限姓关的信息
            menuRoleMapper.deleteBatch(idsList);
            //3.批量删除菜单以及当前菜单下的子菜单列表
            idsList.stream().forEach((mid)->{
                //使用forEach循环读取idsList集合中的菜单ID，在调用deleteByID，
                //实现删除当前菜单时将当前菜单下的子菜单列表都进行删除
                menuMapper.deleteById(mid);
            });
            // 菜单删除成功后，还需从redis中将之前的缓存清除
            clearMenuListFromRedis();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 更新菜单
     * @param menu
     * @return
     */
    @Override
    public boolean updateMenu(Menu menu) {
        if(menuMapper.updateById(menu)>0){
            // 菜单修改成功后，还需从redis中将之前的缓存清除
            clearMenuListFromRedis();
            return true;
        }
        return false;
    }

    /**
     * 菜单新增，删除亦或修改成功后还需要从redis中将之前缓存的菜单列表数据给清空
     */
    @Override
    public void clearMenuListFromRedis() {
        //1.得到当前登录用户的ID
        Integer adminId = ((Admin) (SecurityContextHolder.getContext().getAuthentication().getPrincipal())).getId();
        //2.从redis中清空之前缓存的菜单列表数据
        redisTemplate.delete("menu_"+adminId);
    }

    /**
     * 通过父级权限ID查询所有子级权限再把所有子级权限的ID都保存到idList集合中
     * @param id
     * @param idsList
     */
    private void selectChildMenuById(Integer id, LinkedList<Integer> idsList) {
        //查询菜单里面的子菜单
        List<Menu> childIdList=findById(id);
        //把childIdList里面菜单id值获取出来，封装idList里面，做递归查询
        childIdList.stream().forEach(item->{
            //封装idList里面
            idsList.addFirst(item.getId());
            //递归查询
            this.selectChildMenuById(item.getId(),idsList);
        });
    }

    //id为父级菜单ID
    private List<Menu> findById(Integer id) {
        //通过父级菜单ID查询指定的子级权限列表
        List<Menu> childMenu = menuMapper.selectList(new QueryWrapper<Menu>().eq("parentId", id));
        List<Menu> twoLevelMenu=null;
        if(childMenu.size()>0&&childMenu.get(0).getMenuLevel()!=2){
            //说明当前childMenu集合中保存不是三级权限，比如他是三级权限
            //查询所有的权限(一级，二级，三级)
            List<Menu> allMenu=menuMapper.selectList(null);
            //所以下面需要查询每个二级权限下的三级权限
            twoLevelMenu=childMenu.stream().map((menu)->{
                menu.setChildren(getChildrens(menu,allMenu));
                return menu;
            }).collect(Collectors.toList());
            return twoLevelMenu;
        }
        return childMenu;
    }
}
