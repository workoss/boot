package com.workoss.boot.common.dict;

import com.workoss.boot.util.json.JsonMapper;
import org.junit.jupiter.api.Test;

/**
 * @author workoss
 */
 class DictTest {

    @Test
    void test01(){
        DemoModel demoModel = new DemoModel();
        demoModel.setState(DefaultNullEnum.NO.getCode());
        System.out.println(JsonMapper.toJSONString(demoModel));
    }
}
