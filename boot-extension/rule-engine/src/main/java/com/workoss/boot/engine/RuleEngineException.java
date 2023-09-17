package com.workoss.boot.engine;

import com.workoss.boot.util.exception.BootException;

/**
 * @author workoss
 */
public class RuleEngineException extends BootException {

    public RuleEngineException(String msg) {
        super("-4", msg);
    }

    public RuleEngineException(String code, String msg) {
        super(code, msg);
    }
}
