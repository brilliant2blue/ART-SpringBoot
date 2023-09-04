package com.nuaa.art.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nuaa.art.common.HttpCodeEnum;
import com.nuaa.art.common.model.HttpResult;
import com.nuaa.art.common.utils.LogUtils;
import com.nuaa.art.user.entity.User;
import com.nuaa.art.user.model.LoginInfo;
import com.nuaa.art.user.model.UserInfo;
import com.nuaa.art.user.service.dao.UserService;
import com.nuaa.art.user.service.handler.UserHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.catalina.realm.UserDatabaseRealm;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器
 *
 * @author konsin
 * @date 2023/06/12
 */
@RestController
@Tag(name = "用户管理")
public class UserController {
    @Autowired
    UserHandler userHandler;
    @Resource
    UserService userService;

    /**
     * 登录
     *
     * @param user 用户
     * @return {@link Object}
     */
    @PostMapping("/user/login")
    @Operation(summary = "用户登录")
    public HttpResult<LoginInfo> login(@RequestBody User user) {
        LoginInfo result = userHandler.login(user);
        LogUtils.info(user.getUsername());
        if (result != null) {
            System.out.println(result.menus.toString());
            return HttpResult.success(result);
        } else {
            return HttpResult.fail("用户名或密码错误");
        }
    }

    @PostMapping("/user")
    @Operation(summary = "新建用户")
    public HttpResult<Integer> newUser(@RequestBody User user) {
            if(userService.selectUserByName(user.getUsername()) != null ) {
                return HttpResult.fail("用户名已存在，请重试！");
            } else {
                userService.save(user);
                return HttpResult.success(user.getId());
            }
    }

    @PutMapping("/user")
    @Operation(summary = "更新用户")
    public HttpResult<Integer> editUser(@RequestBody User user) {
            if(userService.getOne(new LambdaQueryWrapper<User>()
                    .eq(User::getUsername, user.getUsername())
                    .ne(User::getId, user.getId())) != null ) {
                return HttpResult.fail("用户名已存在，请重试！");
            } else {
                userService.updateById(user);
                return HttpResult.success(user.getId());
            }
    }

    @DeleteMapping("/user")
    @Operation(summary = "删除用户")
    public HttpResult<Integer> delUser(@RequestBody User user) {
        if(userService.removeById(user))
            return HttpResult.success(user.getId());
        else {
            return HttpResult.fail("删除失败！");
        }
    }

    @GetMapping("users")
    @Operation(summary = "获取所有用户")
    public HttpResult<List<User>> getAllUser(@RequestParam("user") String username) {
        User user = userService.selectUserByName(username);
        if(user != null ) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
            queryWrapper.select(User.class, i-> !i.getColumn().equals("password"));
            return HttpResult.success(userService.list(queryWrapper));
        } else {
            return HttpResult.fail();
        }
    }

    @DeleteMapping("users")
    @Operation(summary = "批量删除用户")
    public HttpResult<List<User>> delUsers(@RequestBody List<User> users) {
        if(userService.removeBatchByIds(users)) {
            return HttpResult.success();
        } else {
            return HttpResult.fail("删除失败！");
        }
    }

}
