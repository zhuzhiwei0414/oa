package cn.zzw.server.controller;/*
 *@Author AWei
 *@Create 2022/2/23-15:58
 *@Description
 */

import cn.zzw.server.pojo.Employee;
import cn.zzw.server.pojo.RespBean;
import cn.zzw.server.pojo.RespPageBean;
import cn.zzw.server.pojo.Salary;
import cn.zzw.server.service.IEmployeeService;
import cn.zzw.server.service.ISalaryService;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/salary/sobcfg")
public class SalarySobCfgController {

    @Resource
    private ISalaryService salaryService;
    @Resource
    private IEmployeeService employeeService;

    @ApiOperation("查询所有工资账套信息")
    @GetMapping("/getSalaries")
    public List<Salary> getSalaries(){
        return salaryService.list();
    }

    @ApiOperation("获取所有员工账套信息")
    @GetMapping("/")
    public RespPageBean getEmployeeWithSalary(@RequestParam(defaultValue = "1") Integer currentPage,
                                                 @RequestParam(defaultValue = "10") Integer size){
        return employeeService.getEmployeeWithSalary(currentPage,size);
    }

    @ApiOperation("更新员工账套信息")
    @PutMapping("/updateSalary")
    public RespBean updateSalary(@RequestParam Integer eid,@RequestParam Integer sid){
        if(employeeService.update(new UpdateWrapper<Employee>().set("salaryId",sid)
                .eq("id",eid))){
            return RespBean.success("修改员工账套信息成功！");
        }
        return RespBean.error("修改员工账套信息失败！");
    }

}
