package com.nuaa.art.vrmverify.model.explanation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 原因
 * @author djl
 * @date 2024-03-30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cause {

    private int position;   // 在反例中的位置
    private String variable;    // 变量

}
