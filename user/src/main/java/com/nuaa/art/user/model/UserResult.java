package com.nuaa.art.user.model;

import com.nuaa.art.user.entity.RoleAccess;
import lombok.Data;

import java.util.List;

@Data
public class UserResult {
    public Boolean result;
    public String username;
    public String rolename;
    public int role;
    public List<RoleAccess> accesses;
    public List<String> accessStrings;
}
