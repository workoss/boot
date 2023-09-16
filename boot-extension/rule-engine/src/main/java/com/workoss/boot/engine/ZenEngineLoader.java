package com.workoss.boot.engine;

/**
 * @author workoss
 */
public class ZenEngineLoader {

    /**
     * zen 规则引擎执行
     *
     * @param decision json 规则集
     * @param input    入参 json
     * @param trace    是否trace
     * @param maxDepth trace
     * @return json 字符串
     */
    static native byte[] evaluate(byte[] decision, byte[] input, boolean trace, int maxDepth);

    /**
     * 校验 json规则
     *
     * @param decision json 规则集
     * @return true/false
     */
    static native boolean validate(byte[] decision);
}
