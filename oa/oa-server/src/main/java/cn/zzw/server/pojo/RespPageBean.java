package cn.zzw.server.pojo;/*
 *@Author AWei
 *@Create 2022/2/10-20:24
 *@Description 分页公共返回对象
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.formula.functions.T;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespPageBean {
    /**
     * 总页数
     */
    private Long total;
    /**
     * 数据list
     */
    private List<?> data;

}
