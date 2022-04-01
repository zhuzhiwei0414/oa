package cn.zzw.server.service.impl;

import cn.zzw.server.mapper.EmployeeMapper;
import cn.zzw.server.mapper.MailLogMapper;
import cn.zzw.server.pojo.*;
import cn.zzw.server.service.IEmployeeService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zzw
 * @since 2022-01-17
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements IEmployeeService {

    @Resource
    private EmployeeMapper employeeMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private MailLogMapper mailLogMapper;

    /**
     * 获取所有员工信息(分页)
     * @param currentPage
     * @param size
     * @param employee
     * @param beginDateScope
     * @return
     */
    @Override
    public RespPageBean getEmployeeByPage(Integer currentPage, Integer size, Employee employee, LocalDate[] beginDateScope) {
        //开启分页
        Page<Employee> page=new Page<>(currentPage,size);
        IPage<Employee> employeeByPage=employeeMapper.getEmployeeByPage(page,employee,beginDateScope);
        RespPageBean respPageBean=new RespPageBean(employeeByPage.getTotal(),employeeByPage.getRecords());
        return respPageBean;
    }

    /**
     * 获取最大工号
     * @return
     */
    @Override
    public RespBean maxWordID() {
        //查询最大工号
        List<Map<String, Object>> maps = employeeMapper.selectMaps(new QueryWrapper<Employee>().select("max(WorkID)"));
        return RespBean.success(null,String.format("%08d",Integer.parseInt(maps.get(0).get("max(WorkID)").toString())+1));
    }

    /**
     * 添加员工
     * @param employee
     * @return
     */
    @Override
    public RespBean addEmp(Employee employee) {
        /**
         * 处理合同期限，保留两位小数
         */
        //合同起始日期
        LocalDate beginContract = employee.getBeginContract();
        //合同终止日期
        LocalDate endContract = employee.getEndContract();
        //计算相差天数
        long days = beginContract.until(endContract, ChronoUnit.DAYS);
        //十进制格式化（小数）
        DecimalFormat decimalFormat=new DecimalFormat("##.00");
        employee.setContractTerm(Double.parseDouble(decimalFormat.format(days/365.00)));
        if(employeeMapper.insert(employee)==1){
            //发送信息
            Employee emp=employeeMapper.getEmployee(employee.getId()).get(0);
            //数据库记录发送的消息
            String msgId = UUID.randomUUID().toString();
//            String msgId="123456";
            MailLog mailLog = new MailLog();
            mailLog.setMsgId(msgId);
            mailLog.setEid(employee.getId());
            mailLog.setStatus(0);
            mailLog.setCount(0);
            mailLog.setRouteKey(MailConstants.MAIL_ROUTING_KEY_NAME);
            mailLog.setExchange(MailConstants.MAIL_EXCHANGE_NAME);
            mailLog.setTryTime(LocalDateTime.now().plusMinutes(MailConstants.MSG_TIMEOUT));
            mailLog.setCreateTime(LocalDateTime.now());
            mailLog.setUpdateTime(LocalDateTime.now());
            mailLogMapper.insert(mailLog);
            rabbitTemplate.convertAndSend(MailConstants.MAIL_EXCHANGE_NAME,MailConstants.MAIL_ROUTING_KEY_NAME,emp,new CorrelationData(msgId));
            return RespBean.success("添加员工信息成功！");
        }
        return RespBean.success("添加员工信息失败！");
    }

    /**
     * 查询员工信息
     * @param id
     * @return
     */
    @Override
    public List<Employee> getEmployee(Integer id) {
        return employeeMapper.getEmployee(id);
    }

    /**
     * @param currentPage 当前页面
     * @param size 每页大小
     * @return 获取员工账套信息
     */
    @Override
    public RespPageBean getEmployeeWithSalary(Integer currentPage, Integer size) {
        Page<Employee> page=new Page<>(currentPage,size);
        IPage<Employee> employeeIPage=employeeMapper.getEmployeeWithSalary(page);
        RespPageBean respPageBean=new RespPageBean(employeeIPage.getTotal(),employeeIPage.getRecords());
        return respPageBean;
    }

    @Override
    public IPage<Employee> selectByPage(Page<Employee> page, String query, String sort, Integer politicsId, Integer deptId, String beginTime, String endTime) {
        return  employeeMapper.selectByPage(page,query,sort, politicsId, deptId, beginTime, endTime);
    }
}
