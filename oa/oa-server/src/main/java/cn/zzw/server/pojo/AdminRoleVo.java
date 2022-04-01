package cn.zzw.server.pojo;/*
 *@Author AWei
 *@Create 2022/3/2-20:14
 *@Description 操作员角色权限
 */

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="用于接收分配角色时的参数", description="")
public class AdminRoleVo {
    private Integer adminId;
    private Integer[] rid;

    public AdminRoleVo(Integer adminId, Integer[] rid) {
        this.adminId = adminId;
        this.rid = rid;
    }
}
