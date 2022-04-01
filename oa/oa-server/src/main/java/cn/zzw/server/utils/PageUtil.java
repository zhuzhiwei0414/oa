package cn.zzw.server.utils;

import java.util.ArrayList;
import java.util.List;

/**分页工具类
 * @Author JieGe
 * @Create 2022/2/8 - 13:57
 * @Description
 */
public class PageUtil<T> {
    private Integer totalCount;  //总记录数
    private Integer currPageNo;   //当前页码
    private List<T> list = new ArrayList<T>();

    public PageUtil() {
    }

    public PageUtil(Integer totalCount, Integer currPageNo, List<T> list) {
        this.totalCount = totalCount;
        this.currPageNo = currPageNo;
        this.list = list;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getCurrPageNo() {
        return currPageNo;
    }

    public void setCurrPageNo(Integer currPageNo) {
        this.currPageNo = currPageNo;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}