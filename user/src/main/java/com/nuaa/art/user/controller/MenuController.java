package com.nuaa.art.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nuaa.art.common.model.HttpResult;
import com.nuaa.art.user.entity.Menu;
import com.nuaa.art.user.model.MenuTree;
import com.nuaa.art.user.service.dao.MenuService;
import com.nuaa.art.user.service.handler.MenuHandler;
import com.nuaa.art.user.service.handler.UserHandler;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MenuController {
    @Resource
    MenuService menuService;
    @Resource
    UserHandler userHandler;
    @Resource
    MenuHandler menuHandler;

    @GetMapping("/user/menus")
    @Operation(summary = "获取权限目录")
    public HttpResult<List<MenuTree>> getlist(){
        List<MenuTree> trees = menuHandler.MenusToMenuTree(menuService.list());
        if(trees != null)
            return HttpResult.success(trees);
        else return HttpResult.fail();
    }

//    @GetMapping("/user/{username}/access")
//    @Operation(summary = "获取权限目录")
//    public HttpResult<UserMenu> getaccess(@PathVariable("username") String userName){
//        UserMenu userMenu = userHandler.getUserAccess(userName);
//        if(userMenu != null)
//            return HttpResult.success(userMenu);
//        else return HttpResult.fail();
//    }

    @PostMapping("/user/menu")
    @Operation(summary = "新建权限目录")
    public HttpResult<Integer> newMenu(@RequestBody Menu menu){
        if(menuService.getOne(new LambdaQueryWrapper<Menu>().eq(Menu::getMenuName, menu.getMenuName()))!= null) {
            return HttpResult.fail("存在同名菜单项！");
        }
        if(menuService.save(menu)){
            return HttpResult.success(menu.getId());
        } else {
            return HttpResult.fail();
        }
    }

    @PutMapping("/user/menu")
    @Operation(summary = "更新权限目录")
    public HttpResult<Integer> editMenu(@RequestBody Menu menu){
        if(menuService.getOne(new LambdaQueryWrapper<Menu>()
                .eq(Menu::getMenuName, menu.getMenuName())
                .ne(Menu::getId, menu.getId()))!= null) {
            return HttpResult.fail("存在同名菜单项！");
        }
        if(menuService.updateById(menu)){
            return HttpResult.success(menu.getId());
        } else {
            return HttpResult.fail();
        }
    }

    @DeleteMapping("/user/menu/{id}")
    @Operation(summary = "删除指定权限")
    public HttpResult<Integer> deleteMenu(@PathVariable("id") Integer id){
        if(menuService.removeById(id)){
            return HttpResult.success();
        } else {
            return HttpResult.fail();
        }
    }

    @DeleteMapping("/user/menus")
    @Operation(summary = "批量删除权限")
    public HttpResult<Integer> deleteMenus(@RequestBody List<Menu> menus){
        //System.out.println(menus.toString());
        if(menuService.removeByIds(menus)){
            return HttpResult.success();
        } else {
            return HttpResult.fail();
        }
    }
}
