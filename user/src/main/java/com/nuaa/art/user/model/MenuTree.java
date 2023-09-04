package com.nuaa.art.user.model;

import com.nuaa.art.user.entity.Menu;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 向前端返回菜单树
 *
 * @author konsin
 * @date 2023/07/20
 */
@Data
public class MenuTree extends Menu {
    List<MenuTree> children = new ArrayList<>();

    public static MenuTree MenuFactory(Menu menu) {
        MenuTree t = new MenuTree();
        t.setId(menu.getId());
        t.setMenuSign(menu.getMenuSign());
        t.setMenuName(menu.getMenuName());
        t.setUrl(menu.getUrl());
        t.setParentId(menu.getParentId());
        t.children = new ArrayList<>();
        return t;
    }
}
