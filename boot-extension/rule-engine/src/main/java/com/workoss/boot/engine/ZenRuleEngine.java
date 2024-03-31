/*
 * Copyright 2019-2024 workoss (https://www.workoss.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.workoss.boot.engine;

import com.fasterxml.jackson.databind.JsonNode;
import com.workoss.boot.util.Lazy;
import com.workoss.boot.util.StringUtils;
import com.workoss.boot.util.json.JsonMapper;
import io.github.workoss.jni.JniLibLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author workoss
 */
@SuppressWarnings("ALL")
public class ZenRuleEngine {

	private static final Logger log = LoggerFactory.getLogger(ZenRuleEngine.class);

	private final ZenRuleEngineConfig config;

	private static final Lazy<ZenRuleEngineConfig> DEFAULT_CONFIG = Lazy.of(() -> {
		ZenRuleEngineConfig config = new ZenRuleEngineConfig();
		config.setTrace(false);
		config.setMaxDepth(5);
		return config;
	});

	public ZenRuleEngine(ZenRuleEngineConfig config) {
		this.config = config;
		try {
			JniLibLoader.getInstance().loadLibrary(ZenRuleEngine.class.getClassLoader(), "zen-engine",false);
		}
		catch (IOException e) {
			throw new RuleEngineException("load lib error");
		}
	}

	/**
	 * 表达式执行
	 * @param expression 表达式
	 * @param json 上下文
	 * @return 表达式结果
	 * @throws RuleEngineException 异常
	 */
	public JsonNode runExpr(String expression, JsonNode json) throws RuleEngineException {
		if (StringUtils.isBlank(expression)) {
			throw new RuleEngineException("expression should not be null");
		}
		byte[] body = ZenEngineLoader.expression(expression.getBytes(StandardCharsets.UTF_8),
				JsonMapper.toJSONBytes(json));
		JsonNode response = JsonMapper.parse(body);
		log.atDebug().log("[EXPRE] expression:{} resp:{}", expression, response);
		return response;
	}

	public JsonNode runExprWithRule(String expression, JsonNode json) throws RuleEngineException {
		if (StringUtils.isBlank(expression)) {
			throw new RuleEngineException("expression should not be null");
		}
		String expressionTpl = "{\"contentType\":\"application/vnd.gorules.decision\",\"edges\":[{\"id\":\"fd58db13-4154-4234-9e35-38c370777219\",\"type\":\"edge\",\"sourceId\":\"adde472f-1eba-4571-af12-3169b8760ea0\",\"targetId\":\"ef22af77-1363-468c-920e-65afde643f45\"},{\"id\":\"a1100bb0-268c-4ae5-a344-529a707ad438\",\"type\":\"edge\",\"sourceId\":\"ef22af77-1363-468c-920e-65afde643f45\",\"targetId\":\"1b68f4b6-4f38-4e89-b9d4-bc74a607dc03\"}],\"nodes\":[{\"id\":\"adde472f-1eba-4571-af12-3169b8760ea0\",\"name\":\"Request\",\"type\":\"inputNode\",\"position\":{\"x\":110,\"y\":180}},{\"id\":\"ef22af77-1363-468c-920e-65afde643f45\",\"name\":\"表达式\",\"type\":\"expressionNode\",\"content\":{\"expressions\":[{\"id\":\"xZmc0Nyjz7\",\"key\":\"data\",\"value\":\"%s\"}]},\"position\":{\"x\":490,\"y\":170},\"description\":\"表达式执行\"},{\"id\":\"1b68f4b6-4f38-4e89-b9d4-bc74a607dc03\",\"name\":\"Response\",\"type\":\"outputNode\",\"position\":{\"x\":910,\"y\":160}}]}";
		;
		EvaluateResult evaluate = evaluate(String.format(expressionTpl, expression).getBytes(StandardCharsets.UTF_8),
				json, DEFAULT_CONFIG.get());
		log.atDebug().log("[RULE_ENGINE] expression:{} cost:{}", expression, evaluate.getPerformance());
		return evaluate.getResult();
	}

	/**
	 * 执行方法
	 * <p>
	 * const handler=(input,{moment,dayjs,Big,env})=>{console.log(input);const
	 * momentValid=typeof
	 * moment==="function"&&Object.keys(moment).includes('isDayjs');const
	 * dayjsValid=typeof dayjs==='function'&&Object.keys(moment).includes('isDayjs');const
	 * bigjsValid=typeof
	 * Big==='function';return{totalAmount:input.price*input.quantity,momentValid,dayjsValid,bigjsValid};}
	 * </p>
	 * @param handlerFunc js handler方法 可以修改body和 return
	 * @param json 上下文
	 * @return js func return
	 * @throws RuleEngineException 异常
	 */
	public JsonNode runFunction(String handlerFunc, JsonNode json) throws RuleEngineException {
		if (StringUtils.isBlank(handlerFunc)) {
			throw new RuleEngineException("handler function should not be null");
		}
		handlerFunc = handlerFunc.replaceAll("\n", StringUtils.EMPTY)
			.replaceAll("\t", " ")
			.replaceAll("\\\\\"", "'")
			.replaceAll("\"", "'");
		ZenRuleEngineConfig zenRuleEngineConfig = DEFAULT_CONFIG.get();
		if (handlerFunc.contains("console.log")) {
			zenRuleEngineConfig.setTrace(true);
			zenRuleEngineConfig.setMaxDepth(5);
		}
		String functionBodyTpl = "{\"contentType\":\"application/vnd.gorules.decision\",\"nodes\":[{\"id\":\"2328557f-58ff-487d-b5c8-a72e140697f0\",\"name\":\"Request\",\"type\":\"inputNode\",\"position\":{\"x\":140,\"y\":100}},{\"id\":\"afef8a1c-1bce-411a-bb02-fb5887d81030\",\"name\":\"JS 函数\",\"type\":\"functionNode\",\"content\":\"%s\",\"position\":{\"x\":450,\"y\":90},\"description\":\"方法\"},{\"id\":\"a821ccb4-3426-4745-8ba4-1cbfd6b4788a\",\"name\":\"Response\",\"type\":\"outputNode\",\"position\":{\"x\":770,\"y\":90}}],\"edges\":[{\"id\":\"7e5054a2-8933-4eed-b568-a7437ad382c2\",\"type\":\"edge\",\"sourceId\":\"2328557f-58ff-487d-b5c8-a72e140697f0\",\"targetId\":\"afef8a1c-1bce-411a-bb02-fb5887d81030\"},{\"id\":\"ca78c2d3-233c-4f8f-afbe-7066f485d07c\",\"type\":\"edge\",\"sourceId\":\"afef8a1c-1bce-411a-bb02-fb5887d81030\",\"targetId\":\"a821ccb4-3426-4745-8ba4-1cbfd6b4788a\"}]}";
		EvaluateResult evaluate = evaluate(String.format(functionBodyTpl, handlerFunc).getBytes(StandardCharsets.UTF_8),
				json, zenRuleEngineConfig);
		log.atDebug().log("[RULE_ENGINE] function:{} cost:{}", handlerFunc, evaluate.getPerformance());
		if (zenRuleEngineConfig.isTrace()) {
			Map<String, EvaluateTraceResult> trace = evaluate.getTrace();
			EvaluateTraceResult traceResult = trace.get("afef8a1c-1bce-411a-bb02-fb5887d81030");
			if (traceResult != null) {
				log.atInfo()
					.log("[RULE_ENGINE] function trace:{}", JsonMapper.toJSONString(traceResult.getTraceData()));
			}
		}
		return evaluate.getResult();
	}

	/**
	 * 执行规则
	 * @param decision 规则内容
	 * @param json 入参
	 * @return 执行结果，错误返回 RuleEngineException 异常
	 */
	public EvaluateResult evaluate(byte[] decision, JsonNode json) throws RuleEngineException {
		return evaluate(decision, json, config);
	}

	public EvaluateResult evaluate(byte[] decision, JsonNode json, ZenRuleEngineConfig customConfig)
			throws RuleEngineException {
		if (decision == null) {
			throw new RuleEngineException("decision should not be null");
		}
		if (json == null) {
			throw new RuleEngineException("input should not be null");
		}
		byte[] result = ZenEngineLoader.evaluate(decision, JsonMapper.toJSONBytes(json), customConfig.isTrace(),
				customConfig.getMaxDepth());
		return JsonMapper.parseObject(result, EvaluateResult.class);
	}

	public boolean validateDecision(byte[] decision) throws RuleEngineException {
		return ZenEngineLoader.validate(decision);
	}

	public boolean validate(byte[] decision) {
		try {
			return validateDecision(decision);
		}
		catch (RuleEngineException e) {
			log.warn("[RULE_ENGINE] valid error:{}", e.toString());
		}
		return false;
	}

}
