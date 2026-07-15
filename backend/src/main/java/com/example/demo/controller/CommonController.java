package com.example.demo.controller;

import com.example.demo.common.exception.BussinessException;
import com.example.demo.common.result.Result;
import com.example.demo.common.result.ResultCode;
import com.example.demo.common.util.Captcha;
import com.example.demo.common.util.CaptchaCache;
import com.example.demo.common.util.UploadFile;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@RestController
public class CommonController {

    @Resource
    private DefaultKaptcha defaultKaptcha;
    @Resource
    private CaptchaCache captchaCache;

    @Value("${file.server.dir}")
    private String fileDir = "";

    @Value("${file.server.path}")
    private String path = "";

    //获取验证码
    @CrossOrigin
    @GetMapping("/common/getCaptcha")
    public Result<Captcha> getCapcha(){
        //根据验证码配置生成验证码文本
        String captchaText = defaultKaptcha.createText();
        String base64Code = "";
        //根据验证码文本生成验证码图片,BufferedImage 是 Java 的图像对象，可以直接写入文件或者转换成流。
        BufferedImage image = defaultKaptcha.createImage(captchaText);
        //ByteArrayOutputStream 是一个内存流
        //核心原因：浏览器只能接收二进制或 Base64 形式的图片，不能直接解析 Java 的 BufferedImage 对象。
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            //把图片写入流，得到二进制数据。
            ImageIO.write(image, "jpg", os);
            //把字节流转成 Base64,Base64 是一种把二进制转换成 ASCII 字符串的方式，可以直接放到 HTML <img> 标签里显示.
            //这样就不需要保存图片到服务器，也不需要单独提供图片 URL。
            base64Code = Base64.getEncoder().encodeToString(os.toByteArray());
        } catch (IOException e) {
            throw new BussinessException(ResultCode.CREAT_CAPTCHA_ERROR);
        }
        Captcha captcha = new Captcha();
        //"data:image/png;base64," 是 Data URL 的前缀，告诉浏览器这是一个 用Base64编码的 图片。
        captcha.setCaptchaImage("data:image/png;base64," + base64Code);
        //生成一个 唯一、不重复、连续的字符串 ID，用来标记当前验证码，方便后端校验用户输入。UUID 是 通用唯一识别码
        //把 UUID 对象转换成字符串形式
        String captchaid = UUID.randomUUID().toString().replace("-", "");
        captcha.setCaptchaId(captchaid);
        captchaCache.storeCaptcha(captchaid, captchaText);

        return Result.data(captcha);
    }



    @RequestMapping("/common/uploadFile")
    @CrossOrigin
    public Result<UploadFile> upload(HttpServletRequest req, HttpServletResponse res, @RequestParam("file") MultipartFile file) throws ServletException, IOException {
        //获取文件名
        String fileName = file.getOriginalFilename();
        //获取文件后缀
        fileName = fileName.substring(fileName.lastIndexOf("."));
        //用时间戳生成唯一文件名
        /**
         * 1️⃣ new Date() → 获取当前时间
         * 2️⃣ SimpleDateFormat("yyyyMMddHHmmss") → 定义时间格式
         * 3️⃣ .format(...) → 将当前时间格式化成字符串，用作文件名
         */
        String fname = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + fileName;
        //获取文件路径头缀
        String basePath = fileDir;
        File dir = new File(basePath);
        //如果文件夹不存在，则创建
        if (!dir.exists()) {
            dir.mkdirs();
        }
        //存入数据库里的文件名
        //File.separator 是 Java 提供的系统独立路径分隔符(linux/或者win\)
        String furl = path + File.separator + fname;
        //拼接完整访问地址
        String url = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath() + "/" + furl;
        File dest = new File(basePath, fname);
        try {
            //保存上传的文件
            file.transferTo(dest);
            //封装返回对象
            UploadFile uploadFile = new UploadFile();
            //相对路径，也是存到数据库里的名称
            uploadFile.setName(furl);
            //浏览器可以访问文件的地址
            uploadFile.setUrl(url);
            System.out.println(uploadFile.getUrl());
            return Result.data(uploadFile);
        } catch (IOException e) {
            return Result.error();
        }
    }




}
