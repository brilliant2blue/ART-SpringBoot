package com.nuaa.art.main.config;

import com.nuaa.art.user.interceptor.LoginInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器配置， 开发时可以把这个类文件注释掉
 *
 * @author konsin
 * @date 2023/07/16
 */
@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {
    @Resource
    LoginInterceptor loginInterceptor;
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(loginInterceptor)
//                .addPathPatterns("/**")
//                .excludePathPatterns(
//                        "/user/login",
//                        "/app-alive",
//                        "/druid/**",
//
//                        "/swagger-resources/**",
//                        "/swagger-ui/**",
//                        "/webjars/**",
//                        "/images/**",
//                        "/swagger-ui.html/**",
//                        "/v3/**",
//                        "/doc.html/**",
//                        "configuration/ui",
//                        "configuration/security"
//                );
//    }
}
