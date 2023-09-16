package com.workoss.boot.engine;

import com.fasterxml.jackson.databind.JsonNode;
import com.workoss.boot.util.NativeLibraryLoader;
import com.workoss.boot.util.json.JsonMapper;

import java.io.IOException;

/**
 * @author workoss
 */
public class ZenRuleEngine {

    private final ZenRuleEngineConfig config;

    public ZenRuleEngine(ZenRuleEngineConfig config) {
        this.config = config;
        try {
            NativeLibraryLoader.getInstance().loadLibrary("zen_engine");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public EvaluateResult evaluate(byte[] decision, JsonNode json) {
        byte[] result = ZenEngineLoader.evaluate(decision, JsonMapper.toJSONBytes(json), config.isTrace(), config.getMaxDepth());
        return JsonMapper.parseObject(result, EvaluateResult.class);
    }

    public boolean validate(byte[] decision) {
        return ZenEngineLoader.validate(decision);
    }

}
