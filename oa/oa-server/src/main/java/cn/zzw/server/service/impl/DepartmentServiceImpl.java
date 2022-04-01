package cn.zzw.server.service.impl;

import cn.zzw.server.mapper.DepartmentMapper;
import cn.zzw.server.pojo.Department;
import cn.zzw.server.pojo.RespBean;
import cn.zzw.server.service.IDepartmentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
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
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements IDepartmentService {

    @Resource
    private DepartmentMapper departmentMapper;

    /**
     * 查询所有的部门列表(带层级关系的部门列表)
     * @return
     */
    @Override
    public List<Department> getAllDepartments() {
        List<Department> departments = this.list();
        List<Department> levelDeptList = departments.stream().filter((dept) -> {
            // 过滤出一级部门
            return dept.getParentId() == -1;
        }).map((oneLevel) -> {
            // 获取一级部门下的子部门集合
            oneLevel.setChildren(getChildren(oneLevel, departments));
            return oneLevel;
        }).collect(Collectors.toList());
        return levelDeptList;
    }

    /**
     * 从所有的部门列表中获取当前部门下的子部门列表
     * @param currDept 当前部门对象
     * @param allDept 所有的部门列表
     * @return
     */
    public List<Department> getChildren(Department currDept, List<Department> allDept) {
        List<Department> children = allDept.stream().filter((dept) -> {
            // 过滤出一级部门下的二级部门
            return dept.getParentId() == currDept.getId();
        }).map((twoLevel) -> { // 使用map遍历二级部门列表
            // 使用递归再调用getChildren()方法实现为每个二级部门查询其三级部门.....
            twoLevel.setChildren(getChildren(twoLevel, allDept));
            return twoLevel;
        }).collect(Collectors.toList());
        return children;
    }

    /**
     * 添加部门
     * @param dep
     * @return
     */
    @Override
    public RespBean addDep(Department dep) {
        dep.setEnabled(true);
        departmentMapper.addDep(dep);
        if (1==dep.getResult()){
            return RespBean.success("添加部门信息成功",dep);
        }
        return RespBean.error("添加部门信息失败");
    }

    /**
     * 删除部门
     * @param id
     * @return
     */
    @Override
    public RespBean deleteDep(Integer id) {
        Department dep=new Department();
        dep.setId(id);
        departmentMapper.deleteDep(dep);
        if(dep.getResult()==-2){
            return RespBean.error("该部门下还有子部门，删除部门信息失败！");
        }else if(dep.getResult()==-1){
            return RespBean.error("该部门下还有员工，删除部门信息失败！");
        }else if(dep.getResult()==1){
            return RespBean.success("删除部门信息成功！");
        }
        return RespBean.success("删除部门信息失败！");
    }
}
