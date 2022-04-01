package cn.zzw.server.pojo;/*
 *@Author AWei
 *@Create 2022/2/24-9:07
 *@Description 消息
 */

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ChatMsg {

    private String from;
    private String to;
    private String content;
    private LocalDateTime date;
    private String formNickName;
}
