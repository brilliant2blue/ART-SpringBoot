package com.nuaa.art.vrm.service.handler.Impl;

import com.nuaa.art.vrm.model.model.VRMOfXML;
import com.nuaa.art.vrm.service.handler.ModelLoadHandler;

public class ModelLoadXML implements ModelLoadHandler {
    @Override
    public VRMOfXML loadModelFile(String name) {
        return new VRMOfXML(name);
    }
}
