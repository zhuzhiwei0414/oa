package cn.zzw.server.service;

import cn.zzw.server.pojo.Employee;
import cn.zzw.server.pojo.RespBean;
import cn.zzw.server.pojo.RespPageBean;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author zzw
 * @since 2022-01-17
 */
public interface IEmployeeService extends IService<Employee> {
    /**
     * 获取所有员工信息(分页)
     * @param currentPage
     * @param size
     * @param employee
     * @param beginDateScope
     * @return
     */
    RespPageBean getEmployeeByPage(Integer currentPage, Integer size, Employee employee, LocalDate[] beginDateScope);

    /**
     * 查询最大工号
     * @return
     */
    RespBean maxWordID();

    /**
     * 新增员工信息
     * @param employee
     * @return
     */
    RespBean addEmp(Employee employee);

    /**
     * 查询员工信息
     * @param id
     * @return
     */
    List<Employee> getEmployee(Integer id);

    /**
     * @param currentPage 当前页面
     * @param size 每页大小
     * @return 获取员工账套信息
     */
    RespPageBean getEmployeeWithSalary(Integer currentPage, Integer size);

    /**
     * 分页显示员工基本资料列表
     * @param page 分页所需要的Page集合
     * @param query 查询条件(按员工名模糊查询)
     */
    IPage<Employee> selectByPage(Page<Employee> page, String query, String sort, Integer politicsId, Integer deptId, String beginTime, String endTime);

}
