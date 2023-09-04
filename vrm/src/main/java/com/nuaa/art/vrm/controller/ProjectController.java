package com.nuaa.art.vrm.controller;

import com.nuaa.art.common.HttpCodeEnum;
import com.nuaa.art.common.model.HttpResult;
import com.nuaa.art.common.utils.LogUtils;
import com.nuaa.art.vrm.entity.SystemProject;
import com.nuaa.art.vrm.service.dao.DaoHandler;
import com.nuaa.art.vrm.service.dao.SystemProjectService;
import com.nuaa.art.vrm.service.handler.CreateProjectHandler;
import com.nuaa.art.vrm.service.handler.ProjectDataHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 项目控制类
 *
 * @author konsin
 * @date 2023/07/05
 */
@RestController
@Tag(name = "项目控制")
public class ProjectController {
    @Resource(name = "createProjectAsNew")
    CreateProjectHandler createAsNew;

    @Resource(name = "createProjectBaseConceptLibrary")
    CreateProjectHandler createBaseConcept;

    @Resource(name = "createProjectBaseProject")
    CreateProjectHandler createBaseProject;
    @Resource
    DaoHandler daoHandler;

    @PostMapping("vrm")
    @Operation(summary = "创建项目",description =
            "方式0：空项目；\n" + "方式1：从领域概念库创建；\n" + "方式2：从已有项目创建\n")
    @Parameter(name = "type",description = "创建方式")
    @Parameter(name = "baseSystemId",description = "基础项目号")
    public HttpResult<Integer> creatProject(@RequestBody SystemProject project, @RequestParam("type") Integer creatType
            , @RequestParam(required = false, name = "baseSystemId")Integer baseSystemId){
        String projectName = project.getSystemName();

        switch (creatType) {
            case 0 -> createAsNew.newProject(project, null);
            case 1 -> createBaseConcept.newProject(project, baseSystemId);
            case 2 -> createBaseProject.newProject(project, baseSystemId);
            default -> {
            }
        }

        if(project.getSystemId() != null || project.getSystemId() > 0){
            LogUtils.info("创建成功： " + projectName);
            return new HttpResult<>(HttpCodeEnum.SUCCESS,project.getSystemId());
        } else {
            LogUtils.info("创建失败请：" + projectName);
            return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED,"创建失败请重试", 0);
        }
    }


    @Resource
    ProjectDataHandler projectDataHandler;
    @DeleteMapping("vrm/{id}")
    @Operation(summary = "删除项目")
    @Parameter(name = "id",description = "项目编号")
    public HttpResult<Boolean> deleteProject(@PathVariable("id") int systemId){
        if(daoHandler.getDaoService(SystemProjectService.class).getSystemProjectById(systemId) == null){
            return new HttpResult<>(HttpCodeEnum.BAD_REQUEST,"系统不存在，请重新选择！",false);
        }
        try {
            projectDataHandler.deleteProject(systemId);
            return new HttpResult<>(HttpCodeEnum.SUCCESS,true);
        } catch (Exception e) {
            return new HttpResult<>(HttpCodeEnum.NOT_FOUND,false);
        }
    }

    @GetMapping("vrm/{id}")
    @Operation(summary = "获取项目名")
    @Parameter(name = "id",description = "项目编号")
    public HttpResult<SystemProject> getProject(@PathVariable("id") int systemId){
        SystemProject project = daoHandler.getDaoService(SystemProjectService.class).getSystemProjectById(systemId);
        if(project != null) {
            return HttpResult.success(project);
        } else {
            return HttpResult.fail();
        }

    }

    @GetMapping("vrm")
    @Operation(summary = "获取所有项目名")
    public HttpResult<List<SystemProject>> listProject() {
        return HttpResult.success(daoHandler.getDaoService(SystemProjectService.class).listSystemProject());
    }

    @GetMapping("vrm/file")
    @Operation(summary = "导出项目")
    @Parameter(name = "id",description = "项目编号")
    @Parameter(name = "fileUrl",description = "文件路径 {path}/{name}.xml")
    public HttpResult<String> exportProjectFile(@RequestParam("id")int systemId, @RequestParam("fileUrl")String fileUrl){
        if(projectDataHandler.exportProjectToFile(systemId,fileUrl)!=null){
            return new HttpResult<>(HttpCodeEnum.SUCCESS,fileUrl);
        } else  {
            return new HttpResult<>(HttpCodeEnum.NOT_FOUND,null);
        }
    }

    @PostMapping("vrm/file")
    @Operation(summary = "导入新项目")
    @Parameter(name = "name",description = "项目名")
    @Parameter(name = "fileUrl",description = "文件路径 {path}/{name}.xml")
    public HttpResult<Integer> importProjectFile(@RequestParam("name")String systemName, @RequestParam("fileUrl")String fileUrl){
        SystemProject sProject = daoHandler.getDaoService(SystemProjectService.class)
                .getSystemProjectByName(systemName);
        if (sProject != null) {
            LogUtils.info("已存在此系统名称，请重新输入名称！");
            new HttpResult<>(HttpCodeEnum.NOT_FOUND, "已存在此系统名称，请重新输入名称！",-1);
        }
        int id =  projectDataHandler.importProjectFromFile(systemName, fileUrl);
        if( id > 0){
            return new HttpResult<>(HttpCodeEnum.SUCCESS,id);
        } else  {
            return new HttpResult<>(HttpCodeEnum.NOT_FOUND,0);
        }
    }


}
