package cn.zzw.server.controller;/*
 *@Author AWei
 *@Create 2022/1/22-21:34
 *@Description 验证码
 */

import com.google.code.kaptcha.impl.DefaultKaptcha;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

@RestController
public class CaptchaController {

    @Resource
    private DefaultKaptcha defaultKaptcha;

    @ApiOperation(value = "验证码")
    @GetMapping(value = "/captcha",produces = "image/jpeg")
    public void captcha(HttpServletRequest request, HttpServletResponse response){
        //定义response输出类型为image/jpeg类型
        response.setDateHeader("Expires",0);
        //Set standara HTTP/1.1 na-cache headers.
        response.setHeader("Cache-Control","no-store, no-cache, must-revalidate");
        //Set IE standara HTTP/1.1 na-cache headers. (use addHeader)
        response.setHeader("Cache-Control","post-check=0, pre-check=0");
        //Set standara HTTP/1.1 na-cache header
        response.setHeader("Pragma","no-cache");
        //return a jpeg
        response.setContentType("image/jpeg");

        //----------------------生成验证码 begin----------------------
        //获取验证码文本内容
        String text=defaultKaptcha.createText();
        System.out.println("验证码内容："+text);
        //将验证码文本内容放入session
        request.getSession().setAttribute("captcha",text);
        //根据文本验证码内容创建图形验证码
        BufferedImage image = defaultKaptcha.createImage(text);
        ServletOutputStream outputStream=null;
        try {
            outputStream=response.getOutputStream();
            //输出流输出图片，格式为jpg
            ImageIO.write(image,"jpg",outputStream);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(null!=outputStream){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //----------------------生成验证码 end----------------------
    }
}
