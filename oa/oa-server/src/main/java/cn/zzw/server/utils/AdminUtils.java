package cn.zzw.server.utils;/*
 *@Author AWei
 *@Create 2022/2/10-18:41
 *@Description 用户工具类
 */

import cn.zzw.server.pojo.Admin;
import org.springframework.security.core.context.SecurityContextHolder;

public class AdminUtils {

    public static Admin getCurrentAdmin(){
        return (Admin)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
