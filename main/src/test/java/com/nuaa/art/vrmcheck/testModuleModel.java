package com.nuaa.art.vrmcheck;

import com.nuaa.art.common.utils.PathUtils;
import com.nuaa.art.main.MainApplication;
import com.nuaa.art.vrm.model.hvrm.HVRM;
import com.nuaa.art.vrm.model.vrm.VRM;
import com.nuaa.art.vrm.service.handler.ModelHandler;
import com.nuaa.art.vrm.service.handler.impl.ModelCreateObjectImpl;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = MainApplication.class)
public class testModuleModel {

    @Resource
    ModelCreateObjectImpl modelCreaet;

    @Resource
    ModelHandler modelHandler;

    @Test
    public void test(){
        String path = "D:\\CodePath\\ART\\ART-SpringBoot\\cache\\test1model.xml";
        System.out.println(path);
        HVRM vrm = modelCreaet.modelFile(0,path );
        List<VRM> models = new ArrayList<>();
        modelHandler.getModuleModels(vrm, models, VRM.class);
        for (VRM m: models) {
            System.out.println(m);
        }
    }
}
