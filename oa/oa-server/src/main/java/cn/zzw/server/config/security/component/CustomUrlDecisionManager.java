package cn.zzw.server.config.security.component;/*
 *@Author AWei
 *@Create 2022/1/31-19:16
 *@Description 权限控制
 * 用于判断访问当前URL所需要的用户角色权限与当前登录用户所拥有的角色权限是否一致
 *  (换句话说，就是判断当前用户是否可以访问当前URL)
 */

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
@Component
public class CustomUrlDecisionManager implements AccessDecisionManager {

    @Override
    public void decide(Authentication authentication, Object o, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
        for (ConfigAttribute configAttribute : configAttributes) {
            //当前Url所需角色
            String needRole = configAttribute.getAttribute();
            //判断角色是否登录，登录即可访问角色，此角色在CustomFilter中设置
            if ("ROLE_LOGIN".equals(needRole)){
                //判断是否登录
                if (authentication instanceof AnonymousAuthenticationToken){
                    throw new AccessDeniedException("尚未登录，请登录！");
                }else{
                    return;
                }
            }
            //判断用户角色是否为url所需角色
            // 3.1 获取当前用户所具有的角色列表
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            // 3.2 遍历这个角色列表
            for (GrantedAuthority authority : authorities) {
                // 遍历所有的角色列表，authority.getAuthority()得到某个角色名
                // 再使用equals判断当前角色名与访问当前url所需要的角色权限是否相等
                if (authority.getAuthority().equals(needRole))
                return; // 相等就直接返回
            }
        }
        // 如果上述整体都不行的话，那就抛出异常
        throw new AccessDeniedException("权限不足，请联系管理员!");
    }

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return false;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }
}
