package com.nuaa.art.vrm.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nuaa.art.common.HttpCodeEnum;
import com.nuaa.art.common.model.HttpResult;
import com.nuaa.art.vrm.entity.Mode;
import com.nuaa.art.vrm.entity.ModeClass;
import com.nuaa.art.vrm.entity.StateMachine;
import com.nuaa.art.vrm.model.EventTable;
import com.nuaa.art.vrm.service.dao.*;
import com.nuaa.art.vrm.service.handler.EventTableHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 领域概念库 模式集控制类
 *
 * @author konsin
 * @date 2023/06/29
 */
@RestController
@Tag(name = "领域概念库 模式集控制类")
public class ModeClassController {
    @Resource
    DaoHandler daoHandler;

    @GetMapping("concept/{id}/modeclasses/{name}")
    @Operation(summary = "获取一个模式集")
    @Parameter(name = "name", description = "模式集名")
    @Parameter(name = "id", description = "项目号")
    public HttpResult<ModeClass> getOne(String name, int id) {
        ModeClass mc = daoHandler.getDaoService(ModeClassService.class).getModeClassByNameandId(name, id);
        if (mc != null) {
            return new HttpResult<>(HttpCodeEnum.SUCCESS, mc);
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_FOUND, null);
        }
    }

    @GetMapping("concept/{id}/modeclasses")
    @Operation(summary = "获取项目下所有模式集")
    @Parameter(name = "id", description = "项目号")
    public HttpResult<List<ModeClass>> getAll(int id) {
        List<ModeClass> mc = daoHandler.getDaoService(ModeClassService.class).listModeClassBySystemId(id);
        if (mc != null) {
            return new HttpResult<>(HttpCodeEnum.SUCCESS, mc);
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_FOUND, null);
        }
    }

    @PostMapping("/concept/{id}/modeclasses/{name}")
    @Operation(summary = "新增一个模式集信息", description = "仅包含模式集的说明信息,不包含具体模式")
    public HttpResult<Integer> newModeClass(@RequestBody ModeClass modeClass, @PathVariable("id") int systemId, @PathVariable("name") String name) {
//        if(modeClass.getModeClassName() == null || modeClass.getModeClassName() == "" || modeClass.getSystemId() == null) {
//            return new HttpResult<>(HttpCodeEnum.BAD_REQUEST,"属性不完整",false);
//        }
        if(daoHandler.getDaoService(ModeClassService.class).getModeClassByNameandId(name, systemId)!= null ){
            return new HttpResult<>(HttpCodeEnum.BAD_REQUEST, "同名变量已存在，请重新填写！",-1);
        }
        if (daoHandler.getDaoService(ModeClassService.class).insertModeClass(modeClass)) {
            return new HttpResult<>(HttpCodeEnum.SUCCESS, modeClass.getModeClassId());
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED, 0);
        }
    }

    @PutMapping("/concept/{id}/modeclasses/{modeclassid}")
    @Operation(summary = "更新一个模式集信息", description = "仅包含模式集的说明信息,不包含具体模式")
    public HttpResult<Integer> updateModeClass(@RequestBody ModeClass modeClass, @PathVariable("modeclassid")Integer systemId,@PathVariable("modeclassid")Integer id) {
//        if(modeClass.getModeClassName() == null || modeClass.getModeClassName() == "" || modeClass.getSystemId() == null) {
//            return new HttpResult<>(HttpCodeEnum.BAD_REQUEST,"属性不完整",false);
//        }
        ModeClass mc = daoHandler.getDaoService(ModeClassService.class).getById(id);
        if(mc != null){
            modeClass.setModeClassId(id);
            ModeClass _mc = daoHandler.getDaoService(ModeClassService.class).getModeClassByNameandId(modeClass.getModeClassName(), systemId);
            if(_mc != null && !_mc.getModeClassId().equals(id)){
                return new HttpResult<>(HttpCodeEnum.BAD_REQUEST, "同名变量已存在，请重新填写！",-1);
            }
            if (daoHandler.getDaoService(ModeClassService.class).updateModeClass(modeClass)) {
                return new HttpResult<>(HttpCodeEnum.SUCCESS, modeClass.getModeClassId());
            } else {
                return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED, 0);
            }
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_FOUND,-1);
        }

    }

    @DeleteMapping("/concept/{id}/modeclasses/{name}")
    @Operation(summary = "删除一个模式集")
    @Parameter(name = "name", description = "模式集名")
    @Parameter(name = "id", description = "项目号")
    //todo 应当删除其包含的模式和模式转换
    public HttpResult<Integer> delModeClass(@PathVariable("id") int systemId, @PathVariable("name") String name) {
        ModeClass mc = daoHandler.getDaoService(ModeClassService.class).getModeClassByNameandId(name, systemId);
        if (mc != null) {
            if (daoHandler.getDaoService(ModeClassService.class).deleteModeClass(mc)) {
                return new HttpResult<>(HttpCodeEnum.SUCCESS, mc.getModeClassId());
            } else {
                return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED, 0);
            }
        } else {
            return new HttpResult<>(HttpCodeEnum.BAD_REQUEST, "领域概念元素不存在", -1);
        }
    }

    @GetMapping("/concept/{id}/modeclasses/{name}/modes")
    @Operation(summary = "获取项目下某一模式集的所有模式")
    public HttpResult<List<Mode>> getModesOfModeClass(@PathVariable("id") int systemId, @PathVariable("name") String name) {
        List<Mode> modes = daoHandler.getDaoService(ModeService.class).listModeByNameandId(name, systemId);
        if (modes != null) {
            return new HttpResult<>(HttpCodeEnum.SUCCESS, modes);
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_FOUND, null);
        }
    }

    @PostMapping("/concept/{id}/modeclasses/{name}/modes/{modename}")
    @Operation(summary = "创建一模式")
    public HttpResult<Integer> newMode(@RequestBody Mode mode, @PathVariable("id") int systemId, @PathVariable("modename") String name) {
        Mode m = daoHandler.getDaoService(ModeService.class).getModeByNameAndId(name, systemId);
        if (m != null) {
            return new HttpResult<>(HttpCodeEnum.BAD_REQUEST, "同名资源已存在，请重新填写！", -1);
        } else {
            if (daoHandler.getDaoService(ModeService.class).insertMode(mode)) {
                return new HttpResult<>(HttpCodeEnum.SUCCESS, mode.getModeId());
            } else {
                return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED, 0);
            }
        }
    }

//    @PostMapping("/concept/{id}/modeclasses/{name}/modes")
//    @Operation(summary = "创建一组模式")
//    public HttpResult<Boolean> newModes(@RequestBody List<Mode> modes){
//        if (daoHandler.getDaoService(ModeService.class).insertModeList(modes)){
//            return new HttpResult<>(HttpCodeEnum.SUCCESS,true);
//        } else {
//            return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED,false);
//        }
//    }

    @PutMapping("/concept/{id}/modeclasses/{name}/modes/{modeid}")
    @Operation(summary = "更新一模式")
    public HttpResult<Integer> updateMode(@RequestBody Mode mode, @PathVariable("id") Integer systemId, @PathVariable("modeid") Integer modeId) {
        Mode m = daoHandler.getDaoService(ModeService.class).getById(modeId);
        if (m != null) {
            mode.setModeId(modeId);
            Mode _m = daoHandler.getDaoService(ModeService.class).getModeByNameAndId(mode.getModeName(), systemId);
            if (_m != null && _m.getModeId().equals(modeId)) {
                return new HttpResult<>(HttpCodeEnum.BAD_REQUEST, "同名变量已存在，请重新填写！", -1);
            }
            if (daoHandler.getDaoService(ModeService.class).updateMode(mode)) {
                return new HttpResult<>(HttpCodeEnum.SUCCESS, mode.getModeId());
            } else {
                return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED, 0);
            }
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_FOUND, -1);
        }
    }

    @DeleteMapping("/concept/{id}/modeclasses/{name}/modes/{modename}")
    @Operation(summary = "删除一模式")
    public HttpResult<Integer> deleteMode(@PathVariable("id") int systemId, @PathVariable("modename") String name) {
        Mode m = daoHandler.getDaoService(ModeService.class).getModeByNameAndId(name, systemId);
        if (m != null) {
            if (daoHandler.getDaoService(ModeService.class).deleteMode(m)) {
                return new HttpResult<>(HttpCodeEnum.SUCCESS, m.getModeId());
            } else {
                return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED, 0);
            }
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_FOUND, -1);
        }
    }

    @DeleteMapping("/concept/{id}/modeclasses/{name}/modes")
    @Operation(summary = "删除项目下所有模式")
    public HttpResult<Integer> deleteModes(@PathVariable("id") int systemId) {
            if (daoHandler.getDaoService(ModeService.class).deleteModeById(systemId)) {
                return new HttpResult<>(HttpCodeEnum.SUCCESS, systemId);
            } else {
                return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED, 0);
            }
    }

    @GetMapping("/concept/{id}/modeclasses/{name}/mode-trans")
    @Operation(summary = "获取某模式集的所有模式转换")
    public HttpResult<List<StateMachine>> getAllModeTrans(@PathVariable("id") int systemId, @PathVariable("name")String denpdencyName) {
        List<StateMachine> mt = daoHandler.getDaoService(StateMachineService.class).listStateMachineByDenpdencyandId(denpdencyName,systemId);
        if(mt != null){
            return new HttpResult<>(HttpCodeEnum.SUCCESS,mt);
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_FOUND,null);
        }
    }

    @GetMapping("/concept/{id}/modeclasses/{name}/mode-trans/{transid}")
    @Operation(summary = "根据模式转换id获取模式指定模式转换")
    public HttpResult<StateMachine> getModeTrans(@PathVariable("transid")int transId){
        StateMachine mt = daoHandler.getDaoService(StateMachineService.class).getById(transId);
        if(mt != null){
            return new HttpResult<>(HttpCodeEnum.SUCCESS,mt);
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_FOUND,null);
        }
    }

    @PostMapping("/concept/{id}/modeclasses/{name}/mode-trans")
    @Operation(summary = "新建一个模式转换")
    public HttpResult<Integer> newModeTrans(@RequestBody StateMachine stateMachine, @PathVariable("id")Integer systemId, @PathVariable("name")String modeClassName){
        QueryWrapper<StateMachine> wrapper = new QueryWrapper<StateMachine>()
                .eq("sourceState", stateMachine.getSourceState())
                .eq("endState",stateMachine.getEndState())
                .eq("dependencyModeClass", modeClassName)
                .eq("systemId", systemId);

        StateMachine sm = daoHandler.getDaoService(StateMachineService.class).getOne(wrapper);
        if(sm != null){
            return new HttpResult<>(HttpCodeEnum.BAD_REQUEST,"已存在相同类型转换，请合并",-1);
        } else {
            stateMachine.setStateMachineId(null);
            if(daoHandler.getDaoService(StateMachineService.class).insertStateMachine(stateMachine)){
                return new HttpResult<>(HttpCodeEnum.SUCCESS, stateMachine.getStateMachineId());
            } else {
                return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED,0);
            }
        }
    }

    @PutMapping("/concept/{id}/modeclasses/{name}/mode-trans/{transid}")
    @Operation(summary = "更新一个模式转换")
    public HttpResult<Integer> newModeTrans(@RequestBody StateMachine stateMachine, @PathVariable("id")Integer systemId,
                                            @PathVariable("name")String modeClassName, @PathVariable("transid")Integer transId){
        if(daoHandler.getDaoService(StateMachineService.class).getStateMachineById(transId) == null){
            return new HttpResult<>(HttpCodeEnum.NOT_FOUND,-1);
        }

        QueryWrapper<StateMachine> wrapper = new QueryWrapper<StateMachine>()
                .eq("sourceState", stateMachine.getSourceState())
                .eq("endState",stateMachine.getEndState())
                .eq("dependencyModeClass", modeClassName)
                .eq("systemId", systemId);

        StateMachine sm = daoHandler.getDaoService(StateMachineService.class).getOne(wrapper);
        if(sm != null && !sm.getStateMachineId().equals(transId)){
            return new HttpResult<>(HttpCodeEnum.BAD_REQUEST,"已存在相同类型转换，请合并",-1);
        } else {
            stateMachine.setStateMachineId(transId);
            if(daoHandler.getDaoService(StateMachineService.class).updateStateMachine(stateMachine)){
                return new HttpResult<>(HttpCodeEnum.SUCCESS, transId);
            } else {
                return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED,0);
            }
        }
    }

    @DeleteMapping("/concept/{id}/modeclasses/{name}/mode-trans/{transid}")
    @Operation(summary = "删除一个模式转换")
    public HttpResult<Integer> newModeTrans(@PathVariable("transid")Integer transId){
        StateMachine sm = daoHandler.getDaoService(StateMachineService.class).getStateMachineById(transId);
        if(sm == null){
            return new HttpResult<>(HttpCodeEnum.NOT_FOUND,-1);
        }
        if(daoHandler.getDaoService(StateMachineService.class).deleteStateMachine(sm)){
                return new HttpResult<>(HttpCodeEnum.SUCCESS, sm.getStateMachineId());
        } else {
                return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED,0);
        }
    }

    @Resource
    EventTableHandler eventTableHandler;
    @PutMapping("/concept/{id}/modeclasses/{name}/mode-trans/{transid}/table")
    @Operation(summary = "将模式转换的表达式转换为表形式进行编辑")
    public HttpResult<EventTable> modeTransToTable(String modeTrans){
        try {
            EventTable eventTable = eventTableHandler.ConvertStringToTable(modeTrans);
            return new HttpResult<>(HttpCodeEnum.SUCCESS, eventTable);
        } catch (Exception e){
            System.out.println(modeTrans);
            return new HttpResult<>(HttpCodeEnum.BAD_REQUEST, e.getMessage(),null);
        }
    }

    @PostMapping("/concept/{id}/modeclasses/{name}/mode-trans/{transid}/string")
    @Operation(summary = "将表形式模式转换转换为表达式进行显示")
    public HttpResult<String> modeTransToTable(EventTable modeTrans){
        try {
            String event = eventTableHandler.ConvertTableToString(modeTrans);
            return new HttpResult<>(HttpCodeEnum.SUCCESS, event);
        } catch (Exception e){
            System.out.println(modeTrans);
            return new HttpResult<>(HttpCodeEnum.BAD_REQUEST, e.getMessage(),null);
        }
    }

}




