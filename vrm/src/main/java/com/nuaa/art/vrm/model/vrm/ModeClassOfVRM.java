package com.nuaa.art.vrm.model.vrm;

import com.nuaa.art.vrm.entity.Mode;
import com.nuaa.art.vrm.entity.ModeClass;
import com.nuaa.art.vrm.entity.StateMachine;
import lombok.Data;

import java.util.ArrayList;

/**
 * vrm模型 模式集
 *
 * @author konsin
 * @date 2023/08/30
 */
@Data
public class ModeClassOfVRM {
    ModeClass modeClass;
    ArrayList<Mode> modes = new ArrayList<>();
    ArrayList<StateMachine> modeTrans = new ArrayList<>();
}
