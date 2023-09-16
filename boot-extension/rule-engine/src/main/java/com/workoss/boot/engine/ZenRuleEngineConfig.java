package com.workoss.boot.engine;

import lombok.Data;

/**
 * @author workoss
 */

@Data
public class ZenRuleEngineConfig {

    private boolean trace;

    private int maxDepth = 10;

}
