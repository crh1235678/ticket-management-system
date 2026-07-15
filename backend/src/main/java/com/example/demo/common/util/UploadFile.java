package com.example.demo.common.util;

import lombok.Data;


@Data
public class UploadFile {
    //文件名/upload/xxx.png
    private String name;
    //文件访问路径，类似 http://localhost:8080/upload/xxx.png
    private String url;

}
