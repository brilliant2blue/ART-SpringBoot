package com.nuaa.art.user;

import cn.hutool.crypto.SecureUtil;
import com.nuaa.art.main.MainApplication;
import com.nuaa.art.user.common.utils.JwtUtils;
import com.nuaa.art.user.common.utils.PasswdUtils;
import com.nuaa.art.user.entity.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
@SpringBootTest(classes = MainApplication.class)
public class testUser {
    @Test
    void testTokenGenerate() {
        User user = new User();
        user.setId(5);
        user.setUsername("root");
        user.setRole(1);
        System.out.println(JwtUtils.generateToken(user));
    }

    @Test
    void testTokenVerify() {
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoxLCJpZCI6NSwidXNlcm5hbWUiOiJyb290IiwianRpIjoidG9rZW5JZCIsImlhdCI6MTY4OTQ4OTg5MiwiZXhwIjoxNjg5NDkyNDg0fQ.wVwdVI4ci2Dpp_uvD4iJq0PvYuJ6gxdhdSS0UVig3VA";
        Claims claims = JwtUtils.verifyJwt(token);

    }

    @Test
    void testSalt(){
        String passwd = "123456";
        System.out.println("普通加密密码: "+ SecureUtil.md5(passwd));
        String salt = PasswdUtils.salt();
        System.out.println("普通加密密码: "+ SecureUtil.md5(passwd + salt));
    }
}
