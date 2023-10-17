package com.nuaa.art.user.common.utils;

import cn.hutool.core.util.StrUtil;
import com.nuaa.art.common.utils.LogUtils;
import com.nuaa.art.user.entity.User;
import com.nuaa.art.user.service.dao.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 *
 * @author konsin
 * @date 2023/07/15
 */

public class JwtUtils {

    /**
     * token过期时间
     */
    private static final long TOKEN_EXPIRED_TIME = 30 *24 *60 *60;

    public static final String jwtId = "tokenId";

    private static final String JWT_SECRET = "Aviation-Requirement-Toolset-NUAA";

    public static String createJWT(Map<String, Object> claims, long time) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256; //指定签名算法
        Date now = new Date(System.currentTimeMillis());
        SecretKey secretKey = generalKey();
        long nowMillis = System.currentTimeMillis();
        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)
                .setId(jwtId)
                .setIssuedAt(now)
                .signWith(secretKey, signatureAlgorithm);

        if(time >= 0){
            long expMillis = nowMillis + time;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp); //设置过期时间
        }
        return builder.compact();
    }

    /**
     * 验证jwt
     *
     * @param token 令牌
     * @return {@link Claims}
     */
    public static Claims verifyJwt(String token) {
        SecretKey secretKey = generalKey();
        Claims claims;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey).build()
                    .parseClaimsJws(token).getBody();
        } catch (Exception e) {
            claims = null;
        }
        return  claims;
    }

    /**
     * 生成Key
     *
     * @return {@link SecretKey}
     */
    public static SecretKey generalKey() {
        byte[] encodeKey = Base64.getEncoder().encode(JWT_SECRET.getBytes());
        SecretKey key = new SecretKeySpec(encodeKey, 0, encodeKey.length, "HmacSHA256");
        return key;
    }

    /**
     * 根据用户信息生成token
     *
     * @param user 用户
     * @return {@link String}
     */
    public static String generateToken(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("username", user.getUsername());
        map.put("role", user.getRole());
        return createJWT(map, TOKEN_EXPIRED_TIME);
    }

    /**
     * 得到当前用户信息
     *
     * @return {@link User}
     */
//    public static User getCurrentUserInfo() {
//        try {
//            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//            String token = request.getHeader("token");
//            if(StrUtil.isNotBlank(token)) {
//                Claims claims = verifyJwt(token);
//                if(claims != null){
//                    return userService.selectUserByName((String)claims.get("username"));
//                } else {
//                    return null;
//                }
//            }
//        } catch (Exception e){
//            return null;
//        }
//        return null;
//    }

    /**
     * 得到用户id
     *
     * @param request 请求
     * @return {@link Integer}
     */
    public static String getUserName(HttpServletRequest request) {
        String token = request.getHeader("token");
        Claims claims = verifyJwt(token);
        if( claims != null) {
            return  (String) claims.get("username");
        } else {
            return "";
        }
    }




}
