package com.nuaa.art.user.service.handler.impl;

import com.nuaa.art.user.entity.Menu;
import com.nuaa.art.user.model.MenuTree;
import com.nuaa.art.user.service.handler.MenuHandler;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class MenuHandlerImpl implements MenuHandler {
    @Override
    public List<MenuTree> MenusToMenuTree(List<Menu> menus) {
        try {
            return toTree(menus);
        } catch (Exception e) {
            return null;
        }



    }

    @Override
    public List<Menu> MenuTreeToMenus(List<MenuTree> menuTree) {
        return null;
    }

    // todo
    public List<MenuTree> toTree(List<Menu> menus){
        List<MenuTree> tree = new ArrayList<>();
        Map<Integer, List<MenuTree>> treeMap = new HashMap<>();
        menus.forEach(menu -> {
            if(treeMap.containsKey(menu.getParentId())) {
                treeMap.get(menu.getParentId()).add(MenuTree.MenuFactory(menu));
            } else {
                treeMap.put(menu.getParentId(), new ArrayList<>());
                treeMap.get(menu.getParentId()).add(MenuTree.MenuFactory(menu));
            }
        });

        treeMap.forEach((parentId, collect) -> {
            if (parentId.equals(0)) {
                tree.addAll(collect);
            }
            collect.forEach(item -> {
                item.setChildren(treeMap.get(item.getId()));
            });
        });
        return tree;
    }


}
