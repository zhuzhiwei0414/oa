package cn.zzw.server.service;

import cn.zzw.server.pojo.Admin;
import cn.zzw.server.pojo.RespBean;
import cn.zzw.server.pojo.Role;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author zzw
 * @since 2022-01-17
 */
public interface IAdminService extends IService<Admin> {

    /**
     * 登陆成功后返回方法
     * @param username
     * @param password
     * @param code
     * @param request
     * @return
     */
    RespBean login(String username, String password, String code, HttpServletRequest request);

    Admin getAdminByUserName(String username);

    /**
     * 通过用户id判断权限
     * @param adminId
     * @return
     */
    List<Role> getRoles(Integer adminId);

    //通过用户ID查询用户所拥有的角色列表，以及每个角色所拥有的权限列表
    List<Role> findRolesAndMenusByAdminId(Integer adminId);


    /**
     * 更新操作员角色信息
     * @param adminId
     * @param rids
     * @return
     */
    RespBean addAdminRole(Integer adminId, Integer[] rids);

    /**
     * 修改当前用户密码
     * @param oldPass 旧密码
     * @param pass 新密码
     * @param adminId 用户ID
     * @return
     */
    RespBean updateAdminPassword(String oldPass, String pass, Integer adminId);

    /**
     * 更新用户头像
     * @param url 路径
     * @param id
     * @param authentication
     * @return
     */
    RespBean updateAdminUserFace(String url, Integer id, Authentication authentication);

    /**
     * 分页显示管理员列表
     * @param page 分页所需要的Page集合
     * @param query 查询条件(按用户名或手机号模糊查询)
     */
    IPage<Admin> selectByPage(Page<Admin> page, String query,String sort);

    // 保存操作员新增信息
    boolean saveAdmin(Admin admin);

    Admin findById(Integer id);

    // 通过操作员用户ID查询其拥有菜单URL集合
    List<String> findMenuUrlByAdminId(Integer adminId);


}
