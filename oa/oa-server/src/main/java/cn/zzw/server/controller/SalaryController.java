package cn.zzw.server.controller;


import cn.zzw.server.pojo.RespBean;
import cn.zzw.server.pojo.Salary;
import cn.zzw.server.service.ISalaryService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author zzw
 * @since 2022-01-17
 *  工资账套控制器
 */
@RestController
@RequestMapping("/salary/sob")
public class SalaryController {

    @Resource
    private ISalaryService salaryService;

    @ApiOperation("查询所有工资账套信息")
    @GetMapping("/")
    public List<Salary> getAllSalary(){
        return salaryService.list();
    }

    @ApiOperation("新增工资账套信息")
    @PostMapping("/insertSal")
    public RespBean insertSalary(@RequestBody  Salary salary){
        if(salaryService.save(salary)){
            return RespBean.success("新增工资账套信息成功！");
        }
        return RespBean.error("新增工资账套信息失败！");
    }

    @ApiOperation("修改工资账套信息")
    @PutMapping("/updateSalary")
    public RespBean updateSalary(@RequestBody Salary salary ){
        if(salaryService.updateById(salary)){
            return RespBean.success("修改工资账套信息成功！");
        }
        return RespBean.error("修改工资账套信息失败！");
    }

    @ApiOperation("删除工资账套信息")
    @DeleteMapping("/dleSalary/{id}")
    public RespBean deleteSalary(@PathVariable Integer id){
        if(salaryService.removeById(id)){
            return RespBean.success("删除工资账套信息成功！");
        }
        return RespBean.error("删除工资账套信息失败！");
    }


}
