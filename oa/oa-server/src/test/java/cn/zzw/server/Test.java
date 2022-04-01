package cn.zzw.server;/*
 *@Author AWei
 *@Create 2022/2/28-18:38
 *@Description
 */

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Test {
    public static void main(String[] args) {
//        /*BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
//        String encode = encoder.encode("123");
//        System.out.println(encode);*/
//        // yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
//        String endpoint = "oss-cn-guangzhou.aliyuncs.com";
//// 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
//        String accessKeyId = "LTAI5tLy94QWg1S5XwuzeCKz";
//        String accessKeySecret = "aJuzBCeJmV3STeCRwSobBQQx8n6oU7";
//
//// 创建OSSClient实例。
//        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
//
//// 填写本地文件的完整路径。如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
//        InputStream inputStream = null;
//        try {
//            inputStream = new FileInputStream("C:\\Users\\Administrator\\Pictures\\Saved Pictures\\dd.jpg");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//// 填写Bucket名称和Object完整路径。Object完整路径中不能包含Bucket名称。
//        ossClient.putObject("oa-09kj", "mv.jpg", inputStream);
//
//// 关闭OSSClient。
//        ossClient.shutdown();
//        System.out.println("上传成功");
    }


}
