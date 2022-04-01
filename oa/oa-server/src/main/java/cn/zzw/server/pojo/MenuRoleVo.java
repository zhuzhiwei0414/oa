package cn.zzw.server.pojo;/*
 *@Author AWei
 *@Create 2022/3/9-14:59
 *@Description 菜单角色关联类
 */

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "用于接收菜单和角色保存的id",description = "")
public class MenuRoleVo {
    private Integer rid;
    private Integer [] mid;

    public MenuRoleVo(Integer rid, Integer[] mid) {
        this.rid = rid;
        this.mid = mid;
    }
}
