package com.nuaa.art.user.common;
public enum RoleConstant {

    ROOT_ROLE(1,"超级管理员"),
    REQUIEMENT_ROLE(2,"需求规范用户"),
    CREATE_ROLE(3,"创建模板用户");


    private Integer roleId;
    private String roleName;

    RoleConstant(Integer roleId,String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public static String getRoleNameById(Integer id) {
        RoleConstant[] roleConstants = RoleConstant.values();
        for(int i=0;i<roleConstants.length;i++) {
            if(roleConstants[i].roleId == id) {
                return roleConstants[i].roleName;
            }
        }
        return null;
    }

}
