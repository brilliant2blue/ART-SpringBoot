package com.nuaa.art.vrmverify;

import com.nuaa.art.main.MainApplication;
import com.nuaa.art.vrmverify.common.exception.InvalidCTLException;
import com.nuaa.art.vrmverify.common.utils.CTLParseUtils;
import com.nuaa.art.vrmverify.model.formula.ctl.CTLFormula;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = MainApplication.class)
class VrmVerifyApplicationTests {

    @Test
    public void testParseCTL() {
        CTLFormula correctCTL1 = CTLParseUtils.parseCTLStr("AG(wolf = TRUE)");
        CTLFormula correctCTL2 = CTLParseUtils.parseCTLStr("EF(wolf = TRUE)");
        CTLFormula correctCTL3 = CTLParseUtils.parseCTLStr("AX(wolf = TRUE)");
        CTLFormula correctCTL4 = CTLParseUtils.parseCTLStr("E[wolf = TRUE U sheep = TRUE]");
        CTLFormula incorrectCTL = CTLParseUtils.parseCTLStr("G(wolf = TRUE)");
    }

}
