package com.nuaa.art.common.utils;

import java.io.File;

/**
 * 路径生成工具。
 *
 * @author konsin
 * @date 2023/06/13
 */
public class PathUtils {

    /**
     * 项目路径
     *
     * @return {@link String}
     */
    public static String ProjectPath(){
        return "".concat(System.getProperty("user.dir"));
    }

    /**
     * 默认路径
     *
     * @return {@link String}
     */
    public static String DefaultPath(){
        return PathUtils.ProjectPath()+ File.separator+"cache"+File.separator;
    }


}
