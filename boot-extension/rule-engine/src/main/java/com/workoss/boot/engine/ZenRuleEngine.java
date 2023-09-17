package com.workoss.boot.engine;

import com.fasterxml.jackson.databind.JsonNode;
import com.workoss.boot.util.NativeLibraryLoader;
import com.workoss.boot.util.json.JsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author workoss
 */
public class ZenRuleEngine {

    private static final Logger log = LoggerFactory.getLogger(ZenRuleEngine.class);

    private final ZenRuleEngineConfig config;

    public ZenRuleEngine(ZenRuleEngineConfig config) {
        this.config = config;
        try {
            NativeLibraryLoader.getInstance().loadLibrary("zen_engine");
        } catch (IOException e) {
            throw new RuleEngineException("load lib error");
        }
    }

    /**
     * 执行规则
     *
     * @param decision 规则内容
     * @param json     入参
     * @return 执行结果，错误返回 RuleEngineException 异常
     */
    public EvaluateResult evaluate(byte[] decision, JsonNode json) throws RuleEngineException {
        if (decision == null) {
            throw new RuleEngineException("decision should not be null");
        }
        if (json == null) {
            throw new RuleEngineException("input should not be null");
        }
        byte[] result = ZenEngineLoader.evaluate(decision, JsonMapper.toJSONBytes(json), config.isTrace(), config.getMaxDepth());
        return JsonMapper.parseObject(result, EvaluateResult.class);
    }

    public boolean validateDecision(byte[] decision) throws RuleEngineException {
        return ZenEngineLoader.validate(decision);
    }

    public boolean validate(byte[] decision) {
        try {
            return validateDecision(decision);
        } catch (RuleEngineException e) {
            log.warn("[RULE_ENGINE] valid error:{}", e.toString());
        }
        return false;
    }

}
