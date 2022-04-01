package cn.zzw.server.service;

import cn.zzw.server.pojo.Department;
import cn.zzw.server.pojo.RespBean;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author zzw
 * @since 2022-01-17
 */
public interface IDepartmentService extends IService<Department> {

    /**
     * 查询所有的部门列表(带层级关系的部门列表)
     * @return
     */
    public List<Department> getAllDepartments();


    /**
     * 添加部门
     * @param dep
     * @return
     */
    RespBean addDep(Department dep);

    /**
     * 删除部门
     * @param id
     * @return
     */
    RespBean deleteDep(Integer id);
}
