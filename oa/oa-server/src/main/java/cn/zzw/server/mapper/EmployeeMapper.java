package cn.zzw.server.mapper;

import cn.zzw.server.pojo.Employee;
import cn.zzw.server.pojo.RespPageBean;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zzw
 * @since 2022-01-17
 */
public interface EmployeeMapper extends BaseMapper<Employee> {

    /**
     * 获取所有员工信息(分页)
     * @param page
     * @param employee
     * @param beginDateScope
     * @return
     */
    IPage<Employee> getEmployeeByPage(Page<Employee> page, @Param("employee") Employee employee, @Param("beginDateScope") LocalDate[] beginDateScope);

    /**
     * 通过id查询员工
     * @param id
     * @return
     */
    List<Employee> getEmployee(Integer id);

    /**
     * @return 获取员工账套信息
     */
    IPage<Employee> getEmployeeWithSalary(Page<Employee> page);

    // 分页显示员工基本资料列表, 并按员工名模糊查询及政治面貌、部门、入职时间查询
    IPage<Employee> selectByPage(Page<Employee> page,
                                 @Param("query") String query,
                                 @Param("sort") String sort,
                                 @Param("politicsId") Integer politicsId,
                                 @Param("deptId") Integer deptId,
                                 @Param("beginTime") String beginTime,
                                 @Param("endTime") String endTime);



}
