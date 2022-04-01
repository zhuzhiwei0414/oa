package cn.zzw.server.task;/*
 *@Author AWei
 *@Create 2022/2/21-20:02
 *@Description 邮件定时发送任务
 */

import cn.zzw.server.pojo.MailConstants;
import cn.zzw.server.pojo.MailLog;
import cn.zzw.server.service.IEmployeeService;
import cn.zzw.server.service.IMailLogService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class MailTask {
    @Resource
    private IMailLogService mailLogService;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private IEmployeeService employeeService;

    /**
     * 邮件发送定时任务
     * 10秒一次
     */
    @Scheduled(cron = "0/10 * * * * ?")
    public void mailTask(){
        // 状态为0且重试时间小于当前时间的才需要重新发送
        List<MailLog> list = mailLogService.list(new QueryWrapper<MailLog>().
                eq("status", 0).lt("tryTime", LocalDateTime.now()));
        list.forEach(mailLog -> {
            // 重试次数超过3次，更新为投递失败，不在重试
            if (3<=mailLog.getCount()){
                mailLogService.update(new UpdateWrapper<MailLog>()
                        .set("status", 2).eq("msgId", mailLog.getMsgId()));
            }
            // 更新重新次数，更新时间，重试时间
            mailLogService.update(new UpdateWrapper<MailLog> ().set("count",mailLog.getCount()+1).set("updateTime",LocalDateTime.now()) .set("tryTime",LocalDateTime.now().plusMinutes(MailConstants.MSG_TIMEOUT))
                    .eq("msgId",mailLog.getMsgId()));
            // 发送消息
            rabbitTemplate.convertAndSend(MailConstants.MAIL_EXCHANGE_NAME, MailConstants.MAIL_ROUTING_KEY_NAME,
                    employeeService, new CorrelationData(mailLog.getMsgId()));
        });
    }
}