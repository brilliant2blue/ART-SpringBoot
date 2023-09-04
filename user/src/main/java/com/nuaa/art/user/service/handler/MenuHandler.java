package com.nuaa.art.user.service.handler;

import com.nuaa.art.user.entity.Menu;
import com.nuaa.art.user.model.MenuTree;

import java.util.List;

public interface MenuHandler {
    List<MenuTree> MenusToMenuTree(List<Menu> menus);
    List<Menu> MenuTreeToMenus(List<MenuTree> menuTree);
}
