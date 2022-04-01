package cn.zzw.server.controller;


import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.zzw.server.pojo.*;
import cn.zzw.server.service.*;
import cn.zzw.server.utils.PageUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author zzw
 * @since 2022-01-17
 */
@RestController
@RequestMapping("/employee/basic")
public class EmployeeController {

    @Resource
    private IEmployeeService employeeService;
    @Resource
    private IDepartmentService departmentService;
    @Resource
    private IJoblevelService joblevelService;
    @Resource
    private IPositionService positionService;
    @Resource
    private IPoliticsStatusService politicsStatusService;
    @Resource
    private INationService nationService;

    /**
     * 显示员工基本资料分页列表信息
     * @param currPageNo 当前页码
     * @param pageSize 每页显示的数据行数
     * @param empName 按员工名进行模糊查询
     * @param politicsId 按政治面貌ID进行精确查询
     * @param beginTime 按入职开始时间进行查询
     * @param endTime 按入职结束时间进行查询
     * @param deptId 按部门ID进行查询
     */
    @ApiOperation("显示员工基本资料分页列表信息")
    @GetMapping("/EmpList")
    public RespBean getPageUserList(
            @RequestParam(value = "currPageNo", defaultValue = "1") Integer currPageNo,
            @RequestParam(value = "pageSize", defaultValue = "5")Integer pageSize,
            @RequestParam(value = "empName", required = false) String empName,
            @RequestParam(value = "politicsId", required = false) Integer politicsId,
            @RequestParam(value = "deptId", required = false) Integer deptId,
            @RequestParam(value = "beginTime", required = false) String beginTime,
            @RequestParam(value = "endTime", required = false) String endTime,
            @RequestParam(value = "sort")String sort) {

        Page<Employee> page = new Page<Employee>(currPageNo, pageSize);
        IPage<Employee> adminIPage = employeeService.selectByPage(page, empName ,sort,politicsId,deptId,beginTime,endTime);
        // 使用 Math.toIntExact将Long类型转换成int类型
        Integer total = Math.toIntExact(adminIPage.getTotal());
        Integer current = Math.toIntExact(adminIPage.getCurrent());
        PageUtil<Employee> pageList = new PageUtil<>(total,current,adminIPage.getRecords());
        return RespBean.success("获取员工基本资料列表成功", pageList);
    }

    @ApiOperation(value = "根据员工ID查询当前员工信息")
    @GetMapping("/getById/{id}")
    public Employee getById(@PathVariable Integer id){
        return employeeService.getById(id);
    }

    @ApiOperation(value = "获取所有所属部门信息")
    @GetMapping("/departmentList")
    public List<Department> getAllDepartment(){
        return departmentService.list();
    }

    @ApiOperation(value = "获取所有职称信息")
    @GetMapping("/jobLevelList")
    public List<Joblevel> getAllJoblevel(){
        return joblevelService.list();
    }

    @ApiOperation(value = "获取所有职位信息")
    @GetMapping("/positionList")
    public List<Position> getAllPosition(){
        return positionService.list();
    }

    @ApiOperation(value = "获取所有政治面貌信息")
    @GetMapping("/politicsStatusList")
    public List<PoliticsStatus> getAllPoliticsStatus(){
        return politicsStatusService.list();
    }

    @ApiOperation(value = "获取所有民族信息")
    @GetMapping("/nationList")
    public List<Nation> getAllNation(){
        return nationService.list();
    }

    @ApiOperation(value = "获取最大工号")
    @GetMapping("/maxWorkID")
    public RespBean maxWordID(){
        return employeeService.maxWordID();
    }

    @ApiOperation(value = "添加员工信息")
    @PostMapping("/addEmp")
    public RespBean addEmployee(@RequestBody Employee employee){
        return employeeService.addEmp(employee);
    }

    @ApiOperation(value = "更新员工信息")
    @PutMapping("/updateEmp")
    public RespBean updateEmp(@RequestBody Employee employee){
        if(employeeService.updateById(employee)){
            return RespBean.success("更新员工信息成功！");
        }
        return RespBean.error("更新员工信息失败！");
    }

    @ApiOperation(value = "删除员工信息")
    @DeleteMapping("/delEmpById/{id}")
    public RespBean deleteEmp(@PathVariable Integer id){
        System.out.println("dd");
        if(employeeService.removeById(id)){
            return RespBean.success("删除员工信息成功！");
        }
        return RespBean.error("删除员工信息失败！");
    }

    @ApiOperation(value = "导出员工数据")
    @GetMapping(value = "/export",produces = "application/octet-stream")
//    @PreAuthorize("hasRole('ROLE_admin')")
//    @PreAuthorize("hasAuthority('/employee/basic/exportData')")
    public void exportEmployee(HttpServletResponse response,@RequestParam(value = "currPageNo", defaultValue = "1") Integer currPageNo,
                               @RequestParam(value = "pageSize", defaultValue = "5")Integer pageSize,
                               @RequestParam(value = "empName", required = false) String empName,
                               @RequestParam(value = "politicsId", required = false) Integer politicsId,
                               @RequestParam(value = "deptId", required = false) Integer deptId,
                               @RequestParam(value = "beginTime", required = false) String beginTime,
                               @RequestParam(value = "endTime", required = false) String endTime,
                               @RequestParam(value = "sort" , required = false)String sort) {
        // 按条件分页查询员工列表信息(实现按条件后分页导出，而不是全部导出)
        Page<Employee> page=new Page<>(currPageNo,pageSize);
        IPage<Employee> adminPage = employeeService.selectByPage(page, empName, sort, politicsId, deptId, beginTime, endTime);
        //得到按条件查询并分页显示的员工列表信息
        List<Employee> list = adminPage.getRecords();
        String title="员工表"+System.currentTimeMillis();
        //ExportParam对象的参数一title为导出的标题，参数二为sheetName值，参数三导出后的excel类型
        // 其中HSSF为2003版本，XSSF为2007版本，我们这里选择兼容性更强的HSSF
        ExportParams params = new ExportParams(title, "员工表", ExcelType.HSSF);
        //创建工作簿对象
        Workbook workbook = ExcelExportUtil.exportExcel(params, Employee.class, list);
        //创建输出流对象
        ServletOutputStream out = null;
        try {
            //流形式
            response.setHeader("content-type", "application/octet-stream");
            //防止中文乱码
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("员工表.xls", "UTF-8"));
            out = response.getOutputStream();
            workbook.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @ApiOperation(value = "导入员工数据")
    @RequestMapping("/import")
//    @PreAuthorize("hasRole('ROLE_admin')")
//    @PreAuthorize("hasAuthority('/employee/basic/importData')")
    public RespBean importEmployee(MultipartFile file){
        ImportParams params=new ImportParams();
        //1、去掉标题行(因为导入的Excel表格的前两行是标题，这里需要去掉它)
        params.setTitleRows(1);
        List<Nation> nationList = nationService.list();
        List<Department> departmentList = departmentService.list();
        List<Position> positionList = positionService.list();
        List<Joblevel> joblevelList = joblevelService.list();
        List<PoliticsStatus> politicsStatusList = politicsStatusService.list();
        try {
            //2、调用ExcelImportUtil工具为importExcel()方法得到Excel表中所有的员工信息
            List<Employee> list = ExcelImportUtil.importExcel(file.getInputStream(), Employee.class, params);
            // 使用forEach遍历导入的Excel表格里的员工基本信息，由于Excel基本信息中部门、职位等列是中文名，而我们插入到
            // t_employee表的数据是所对应的部门、职位等的ID值，所以下面要通过中文名获取所对应的ID值
            list.forEach(employee -> {
                //民族ID
                employee.setNationId(nationList.get(nationList.indexOf(new Nation(employee.getNation().getName()))).getId());
                //政治面貌ID
                employee.setPoliticId(politicsStatusList.get(politicsStatusList.indexOf(new PoliticsStatus(employee.getPoliticsStatus().getName()))).getId());
                //部门ID
                employee.setDepartmentId(departmentList.get(departmentList.indexOf(new Department(employee.getDepartment().getName()))).getId());
                //职位ID
                employee.setPosId(positionList.get(positionList.indexOf(new Position(employee.getPosition().getName()))).getId());
                //职称ID
                employee.setJobLevelId(joblevelList.get(joblevelList.indexOf(new Joblevel(employee.getJoblevel().getName()))).getId());
            });
            if(employeeService.saveBatch(list)){
                return RespBean.success("导入员工信息成功！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return RespBean.error("导入员工信息失败！");
    }

}
