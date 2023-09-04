package com.nuaa.art.user.interceptor;

import com.alibaba.druid.pool.DruidDataSource;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuaa.art.common.HttpCodeEnum;
import com.nuaa.art.common.model.HttpResult;
import com.nuaa.art.common.utils.LogUtils;
import com.nuaa.art.user.common.utils.JwtUtils;
import com.nuaa.art.user.mapper.UserMapper;
import com.nuaa.art.user.service.dao.UserService;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.juli.logging.Log;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * 登录拦截器
 *
 * @author konsin
 * @date 2023/07/15
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Resource
    UserService userService;
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        //return HandlerInterceptor.super.preHandle(request, response, handler);
//        long startTime = System.currentTimeMillis();
//        request.setAttribute("requestStartTime", startTime);

        //测试数据库是否启动
//        try {
//            dataSource.setMaxWait(10);
//            dataSource.getConnection();
//        } catch (SQLException e) {
//            response.setCharacterEncoding("UTF-8");
//            response.setContentType("application/json; charset=utf-8");
//            HttpResult resultBody = new HttpResult(HttpCodeEnum.GONE, "数据库链接失败！");
//            ObjectMapper objectMapper = new ObjectMapper();
//            response.getWriter().println(objectMapper.writeValueAsString(resultBody));
//            return false;
//        }

        //OPTIONS请求不校验
        if(request.getMethod().toUpperCase().equals("OPTIONS")) {
            return true;
        }

        String path = request.getRequestURI().toString();
        LogUtils.info("接口登录拦截， path: "+ (path));

        String token = request.getHeader("token");//.replace("\"","");
        LogUtils.info("登录校验开始， token: "+ (token));

        if (token == null || token.isEmpty()) {
            LogUtils.info("token为空");
            falseResult(response);
            return false;
        }

        Claims claims= JwtUtils.verifyJwt(token);

        if (claims == null) {
            LogUtils.info("token无效，请求拦截");
            falseResult(response);
            return false;
        } else {
            if(userService.selectUserByName((String) claims.get("username")) != null){
                LogUtils.info("用户已登录： " + claims.get("username"));
                return true;
            }
        }
        falseResult(response);
        return false;
    }

    public void falseResult(HttpServletResponse response) throws Exception {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        HttpResult resultBody = new HttpResult(HttpCodeEnum.UNAUTHORIZED);
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().println(objectMapper.writeValueAsString(resultBody));
        return;
    }
}
