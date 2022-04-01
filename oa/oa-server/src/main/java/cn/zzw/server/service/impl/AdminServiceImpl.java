package cn.zzw.server.service.impl;

import cn.zzw.server.config.security.component.JwtTokenUtil;
import cn.zzw.server.mapper.AdminRoleMapper;
import cn.zzw.server.mapper.RoleMapper;
import cn.zzw.server.pojo.Admin;
import cn.zzw.server.pojo.AdminRole;
import cn.zzw.server.pojo.RespBean;
import cn.zzw.server.pojo.Role;
import cn.zzw.server.mapper.AdminMapper;
import cn.zzw.server.service.IAdminService;
import cn.zzw.server.service.IMenuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements IAdminService {

    @Resource
    private AdminMapper adminMapper;
    @Resource
    private UserDetailsService userDetailsService;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private JwtTokenUtil jwtTokenUtil;
    @Value("${jwt.tokenHead}")
    private String tokenHead;
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private AdminRoleMapper adminRoleMapper;
    @Resource
    private IMenuService menuService;
    /**
     * 登陆之后返回token
     * @param username
     * @param password
     * @param code
     * @param request
     * @return
     */
    @Override
    public RespBean login(String username, String password, String code, HttpServletRequest request) {
        String captcha = (String) request.getSession().getAttribute("captcha");//验证码
        if(StringUtils.isEmpty(code)||!captcha.equalsIgnoreCase(code)){
            return RespBean.error("验证码输入错误，请重新输入！");
        }
        //登录
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if(userDetails==null||!passwordEncoder.matches(password,userDetails.getPassword())){
            return RespBean.error("用户名或密码不正确，请重新输入！");
        }
        if (!userDetails.isEnabled()){
            return RespBean.error("账号被禁用，请联系管理员！");
        }

        //更新security登录用户对象
        UsernamePasswordAuthenticationToken authenticationToken=
                new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        Admin admin = null;
        try {
            admin = (Admin)userDetailsService.loadUserByUsername(username);
        }catch(UsernameNotFoundException e) {
            return RespBean.error(e.getMessage());
        }
        //生成token
        String token = jwtTokenUtil.generateToken(userDetails);
        Map<String,String> tokenMap=new HashMap<>();
        tokenMap.put("token",token);
        tokenMap.put("tokenHead",tokenHead);
        tokenMap.put("avatar",admin.getUserFace());
        return RespBean.success("登陆成功",tokenMap);
    }

    @Override
    public Admin getAdminByUserName(String username) {
        return adminMapper.selectOne(new QueryWrapper<Admin>()
                .eq("username",username).eq("state",true));
    }

    /**
     * 通过操作员用户ID查询其拥有的角色列表
     * @param adminId
     * @return
     */
    @Override
    public List<Role> getRoles(Integer adminId) {
        return roleMapper.getRoles(adminId);
    }

    //通过用户ID查询用户所拥有的角色列表，以及每个角色所拥有的权限列表
    @Override
    public List<Role> findRolesAndMenusByAdminId(Integer adminId) {
        List<Role> roles=roleMapper.getRoles(adminId).stream().map((role)->{
          //将角色下的权限查询出来
          role.setChildren(roleMapper.findMenuByRole(role.getId()));
          return role;
        }).collect(Collectors.toList());
        return roles;
    }

    /**
     * 为操作员添加角色权限
     * @param adminId
     * @param rids
     * @return
     */
    @Override
    public RespBean addAdminRole(Integer adminId, Integer[] rids) {
        adminRoleMapper.delete(new QueryWrapper<AdminRole>().eq("adminId",adminId));
        Integer result=adminRoleMapper.addAdminRole(adminId,rids);
        if (rids.length==result){
            return RespBean.success("操作员添加角色权限成功！");
        }
        return RespBean.error("操作员添加角色权限失败！");
    }

    /**
     * 修改当前用户密码
     * @param oldPass 旧密码
     * @param pass 新密码
     * @param adminId 用户ID
     * @return
     */
    @Override
    public RespBean updateAdminPassword(String oldPass, String pass, Integer adminId) {
        Admin admin = adminMapper.selectById(adminId);
        //解码
        BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
        //判断输入的密码是否相同
        if(encoder.matches(oldPass,admin.getPassword())){
            //修改旧密码
            admin.setPassword(encoder.encode(pass));
            if(adminMapper.updateById(admin)==1){
                return RespBean.success("更新当前用户密码成功！");
            }
        }
        return RespBean.error("更新当前用户密码失败！");
    }

    /**
     * 更新用户头像
     * @param url 路径
     * @param id
     * @param authentication
     * @return
     */
    @Override
    public RespBean updateAdminUserFace(String url, Integer id, Authentication authentication) {
        Admin admin = adminMapper.selectById(id);
        admin.setUserFace(url);
        if(adminMapper.updateById(admin)==1){
            Admin principal = (Admin) authentication.getPrincipal();
            principal.setUserFace(url);
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken
                    (admin,null,authentication.getAuthorities()));
            return RespBean.success("更新用户头像成功！");
        }
        return RespBean.error("更新用户头像失败！");
    }

    // 分页显示管理员列表, 并按用户名或手机号模糊查询
    @Override
    public IPage<Admin> selectByPage(Page<Admin> page, String query,String sort) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<Admin>();
        queryWrapper.like("name", query).or().like("phone", query);
        // 按id字段进行升序或降序排(取决限前端点击id所在列头)
        if (sort.equals("+id")) {
            queryWrapper.orderByAsc("id");
        } else {
            queryWrapper.orderByDesc("id");
        }
        IPage<Admin> adminPage = adminMapper.selectPage(page, queryWrapper);
        // 遍历所有的操作员对象，为每个操作员对象查询出其角色列表信息出来
        adminPage.getRecords().stream().map((admin) ->{
            // 通过用户ID查询用户所拥有的角色列表，以及每个角色所拥有的权限列表
            admin.setRoles(findRolesAndMenusByAdminId(admin.getId()));
            return admin;
        }).collect(Collectors.toList());
        return adminPage;
    }

    // 保存操作员新增信息
    @Override
    public boolean saveAdmin(Admin admin) {
        if (adminMapper.insert(admin) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public Admin findById(Integer id) {
        Admin admin = adminMapper.selectById(id);
        admin.setRoles(getRoles(admin.getId()));
        return admin;
    }

    // 通过操作员用户ID查询其拥有菜单URL集合
    @Override
    public List<String> findMenuUrlByAdminId(Integer adminId) {
        return adminMapper.findMenuUrlByAdminId(adminId);
    }

}
