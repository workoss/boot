package com.workoss.boot.common.dict;

import com.workoss.boot.common.annotation.Dict;
import lombok.Data;

/**
 * @author workoss
 */
@Data
public class DemoModel {

    @Dict
    private Integer state;

}
