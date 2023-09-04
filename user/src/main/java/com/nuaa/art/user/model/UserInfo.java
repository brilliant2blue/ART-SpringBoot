package com.nuaa.art.user.model;

import com.nuaa.art.user.entity.RoleAccess;
import lombok.Data;

import java.util.List;

@Data
public class UserInfo {
    public String username;
    public String realname;
    public String email;
    public String rolename;
    public int role;
}