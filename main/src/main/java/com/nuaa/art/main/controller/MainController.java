package com.nuaa.art.main.controller;

import com.nuaa.art.common.model.HttpResult;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
