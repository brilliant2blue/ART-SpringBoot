package com.nuaa.art.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nuaa.art.common.model.HttpResult;
import com.nuaa.art.user.entity.*;
import com.nuaa.art.user.service.dao.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Tag(name = "权限管理")
public class RoleContoller {
    @Resource
    RoleService roleService;
    @Resource
    RoleMenuService roleMenuService;

    @Resource
    MenuService menuService;

    @GetMapping("user/roles")
    @Operation(summary = "获取所有角色")
    public HttpResult<List<Role>> listRole() {
        return HttpResult.success(roleService.list());
    }

    @PostMapping("user/role")
    @Operation(summary = "创建角色")
    public HttpResult<Integer> creatRole(@RequestBody Role role) {
        if(roleService.getRoleByRoleId(role.getId()) != null){
            return HttpResult.fail("权限等级已存在！");
        }
        if(roleService.save(role)){
            return HttpResult.success(role.getId());
        } else {
            return HttpResult.fail("创建失败！");
        }
    }

    @PutMapping("user/role")
    @Operation(summary = "更新角色")
    public HttpResult<Integer> updateRole(@RequestBody Role role) {
        if(roleService.getRoleByRoleId(role.getId()) != null){
            return HttpResult.fail("权限等级已存在！");
        }
        if(roleService.updateById(role)){
            return HttpResult.success(role.getId());
        } else {
            return HttpResult.fail("创建失败！");
        }
    }

    @DeleteMapping("user/role/{id}")
    @Operation(summary = "删除角色")
    public HttpResult delRole(@PathVariable("id") Integer id) {
        if(roleService.removeById(id)){
            return HttpResult.success();
        } else {
            return HttpResult.fail("操作失败");
        }
    }

    @DeleteMapping("user/roles")
    @Operation(summary = "删除角色")
    public HttpResult delRoles(@RequestBody List<Role> roles) {
        if(roleService.removeByIds(roles)){
            return HttpResult.success();
        } else {
            return HttpResult.fail("操作失败");
        }
    }

    @GetMapping("user/role/{roleid}/menus")
    @Operation(summary = "获取角色等级下的所有权限")
    public HttpResult<List<Menu>> listRoleMenu(@PathVariable("roleid") Integer roleId) {
        List<Menu> menu=  new ArrayList<>();
        for(RoleMenu ra: roleMenuService.listRoleMenu(roleId)){
            menu.add(menuService.getMenu(ra.getMenuId()));
        }
        return HttpResult.success(menu);
    }

    @PutMapping("user/role/{roleid}/menus")
    @Operation(summary = "更新角色等级下的权限")
    @Transactional
    public HttpResult updateRoleMenu(@RequestBody List<Menu> menuList,
                                                     @PathVariable("roleid") Integer roleId) {
        roleMenuService.remove(new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleId, roleId));
        List<RoleMenu> data = new ArrayList<>();
        for(Menu menu: menuList){
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menu.getId());
            data.add(roleMenu);
        }
        if(data.isEmpty() || roleMenuService.saveBatch(data) )
            return HttpResult.success();
        else
            return HttpResult.fail("修改失败！");
    }
}
