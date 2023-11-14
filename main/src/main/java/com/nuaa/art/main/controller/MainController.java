package com.nuaa.art.main.controller;

import com.nuaa.art.common.model.HttpResult;
import com.nuaa.art.common.utils.FileUtils;
import com.nuaa.art.common.utils.LogUtils;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.SQLException;

@RestController
public class MainController {

    @GetMapping("/app-alive")
    @Operation(summary = "获取服务启动状态")
    public HttpResult alive(){
        return HttpResult.success();
    }

    @GetMapping("/app-authority")
    @Operation(summary = "获取服务是否允许")
    public HttpResult status(){
        return HttpResult.success();
    }



    @PostMapping("/file")
    @Operation(summary = "下载文件")
    public void download(@RequestParam(value = "filename")String fileName, HttpServletResponse response){
        LogUtils.warn(fileName);
        FileUtils.download(fileName,response);
    }

}
