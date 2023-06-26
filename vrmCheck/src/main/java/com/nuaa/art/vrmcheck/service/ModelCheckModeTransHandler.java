package com.nuaa.art.vrmcheck.service;

import com.nuaa.art.vrmcheck.model.CheckErrorReporter;
import com.nuaa.art.vrm.model.model.VRMOfXML;

public interface ModelCheckModeTransHandler {
    boolean checkModeTrans(VRMOfXML vrmModel, CheckErrorReporter errorReporter);
}
