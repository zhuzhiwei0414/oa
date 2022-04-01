package cn.zzw.server.config.security.component;/*
 *@Author AWei
 *@Create 2022/1/31-18:34
 *@Description 权限控制
 *  根据请求url分析请求所需的角色
 */

import cn.zzw.server.pojo.Menu;
import cn.zzw.server.pojo.Role;
import cn.zzw.server.service.IMenuService;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

@Component
public class CustomFilter implements FilterInvocationSecurityMetadataSource {

    @Resource
    private IMenuService menuService;

    AntPathMatcher antPathMatcher=new AntPathMatcher();

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        //获取请求的url
        String requestUrl = ((FilterInvocation) object).getRequestUrl();
        // 2、根据角色获取菜单列表
        List<Menu> menus = menuService.getMenusWithRole();
        for (Menu menu : menus) {
            //判断请求url是否与否与菜单角色所匹配
            if (antPathMatcher.match(menu.getUrl(),requestUrl)){
                // 如果两个url是匹配的，把匹配得上的角色取出来赋值给String[] str角色名数组
                String[] str = menu.getRoles().stream().map(Role::getName).toArray(String[]::new);
                // 再把角色名数组通过createList()方法存放list中
                return SecurityConfig.createList(str);
            }
        }
        //没有匹配的默认登录即可访问
        return SecurityConfig.createList("ROLE_LOGIN");
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }
}
