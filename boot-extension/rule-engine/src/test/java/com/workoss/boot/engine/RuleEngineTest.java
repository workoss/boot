/*
 * Copyright 2019-2023 workoss (https://www.workoss.com)
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
import com.workoss.boot.util.StringUtils;
import com.workoss.boot.util.json.JsonMapper;
import com.workoss.boot.util.text.EscapeUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

/**
 * @author workoss
 */
public class RuleEngineTest {

	private static ZenRuleEngine engine;

	@BeforeAll
	static void setUp() {
		ZenRuleEngineConfig config = new ZenRuleEngineConfig();
		config.setTrace(false);
		config.setMaxDepth(20);
		engine = new ZenRuleEngine(config);
	}

	@Test
	void test01() throws InterruptedException {
		String decision = "{\"contentType\":\"application/vnd.gorules.decision\",\"edges\":[{\"id\":\"1dfbc57d-ad1f-4cf8-978a-e43241856fc8\",\"type\":\"edge\",\"sourceId\":\"be0a5c2d-538d-4e50-9843-91b274e1b9d8\",\"targetId\":\"7c07550a-07bc-4ee4-80d6-9ad900e3d6c9\"},{\"id\":\"5e2dc187-3b82-4f21-94f5-9c3fd16e12d4\",\"type\":\"edge\",\"sourceId\":\"7c07550a-07bc-4ee4-80d6-9ad900e3d6c9\",\"targetId\":\"72d52cfc-e866-4c11-bf79-6be9f750e4d7\"}],\"nodes\":[{\"id\":\"be0a5c2d-538d-4e50-9843-91b274e1b9d8\",\"name\":\"Request\",\"type\":\"inputNode\",\"position\":{\"x\":180,\"y\":280}},{\"id\":\"7c07550a-07bc-4ee4-80d6-9ad900e3d6c9\",\"name\":\"Fees\",\"type\":\"decisionTableNode\",\"content\":{\"hitPolicy\":\"first\",\"inputs\":[{\"id\":\"FD4qBBPv2G\",\"name\":\"Cart Total\",\"type\":\"expression\",\"field\":\"cart.total\"},{\"id\":\"DA3Ybo-shA\",\"name\":\"Customer Country\",\"type\":\"expression\",\"field\":\"customer.country\"},{\"id\":\"jrsT5Wg9F8\",\"name\":\"Customer Tier\",\"type\":\"expression\",\"field\":\"customer.tier\"}],\"outputs\":[{\"id\":\"qA7iYc3Wle\",\"name\":\"Fees Flat ($)\",\"type\":\"expression\",\"field\":\"fees.flat\"},{\"id\":\"JuUcECFGe1\",\"name\":\"Fees Percent\",\"type\":\"expression\",\"field\":\"fees.percent\"}],\"rules\":[{\"_id\":\"vCqrZGdWjA\",\"DA3Ybo-shA\":\"\\\"US\\\"\",\"FD4qBBPv2G\":\"> 1000\",\"JuUcECFGe1\":\"2\",\"jrsT5Wg9F8\":\"\\\"gold\\\"\",\"qA7iYc3Wle\":\"\"},{\"_id\":\"CpXx-s78FH\",\"DA3Ybo-shA\":\"\\\"US\\\"\",\"FD4qBBPv2G\":\"> 1000\",\"JuUcECFGe1\":\"3\",\"jrsT5Wg9F8\":\"\",\"qA7iYc3Wle\":\"\"},{\"_id\":\"zH-PuRB2aQ\",\"DA3Ybo-shA\":\"\\\"US\\\"\",\"FD4qBBPv2G\":\"\",\"JuUcECFGe1\":\"\",\"jrsT5Wg9F8\":\"\",\"qA7iYc3Wle\":\"25\"},{\"_id\":\"HjEp-eQhAP\",\"DA3Ybo-shA\":\"\\\"CA\\\",\\\"MX\\\"\",\"FD4qBBPv2G\":\"> 1000\",\"JuUcECFGe1\":\"5\",\"jrsT5Wg9F8\":\"\",\"qA7iYc3Wle\":\"\"},{\"_id\":\"nq40hJ1nXy\",\"DA3Ybo-shA\":\"\\\"CA\\\",\\\"MX\\\"\",\"FD4qBBPv2G\":\"\",\"JuUcECFGe1\":\"\",\"jrsT5Wg9F8\":\"\",\"qA7iYc3Wle\":\"50\"},{\"_id\":\"G64ltgrVgV\",\"DA3Ybo-shA\":\"\\\"IE\\\",\\\"UK\\\",\\\"FR\\\",\\\"DE\\\"\",\"FD4qBBPv2G\":\"> 1000\",\"JuUcECFGe1\":\"10\",\"jrsT5Wg9F8\":\"\",\"qA7iYc3Wle\":\"\"},{\"_id\":\"PD3oYgtiDa\",\"DA3Ybo-shA\":\"\\\"IE\\\",\\\"UK\\\",\\\"FR\\\",\\\"DE\\\"\",\"FD4qBBPv2G\":\"\",\"JuUcECFGe1\":\"\",\"jrsT5Wg9F8\":\"\",\"qA7iYc3Wle\":\"100\"},{\"_id\":\"1BY7iMFoDw\",\"DA3Ybo-shA\":\"\",\"FD4qBBPv2G\":\"> 1000\",\"JuUcECFGe1\":\"15\",\"jrsT5Wg9F8\":\"\",\"qA7iYc3Wle\":\"\"},{\"_id\":\"dlwJlbW7ZH\",\"DA3Ybo-shA\":\"\",\"FD4qBBPv2G\":\"\",\"JuUcECFGe1\":\"\",\"jrsT5Wg9F8\":\"\",\"qA7iYc3Wle\":\"150\"}]},\"position\":{\"x\":420,\"y\":280}},{\"id\":\"72d52cfc-e866-4c11-bf79-6be9f750e4d7\",\"name\":\"Response\",\"type\":\"outputNode\",\"position\":{\"x\":670,\"y\":280}}]}";
		String input = "{ \"input\": 12 }";
		EvaluateResult result = engine.evaluate(decision.getBytes(StandardCharsets.UTF_8), JsonMapper.parse(input));
		System.out.println(JsonMapper.toJSONString(result));
		boolean validate = engine.validateDecision("多哦少".getBytes(StandardCharsets.UTF_8));
		System.out.println(validate);
	}

	@Test
	void testExpression() {
		// String decision =
		// "{\"contentType\":\"application/vnd.gorules.decision\",\"edges\":[{\"id\":\"fd58db13-4154-4234-9e35-38c370777219\",\"type\":\"edge\",\"sourceId\":\"adde472f-1eba-4571-af12-3169b8760ea0\",\"targetId\":\"ef22af77-1363-468c-920e-65afde643f45\"},{\"id\":\"a1100bb0-268c-4ae5-a344-529a707ad438\",\"type\":\"edge\",\"sourceId\":\"ef22af77-1363-468c-920e-65afde643f45\",\"targetId\":\"1b68f4b6-4f38-4e89-b9d4-bc74a607dc03\"}],\"nodes\":[{\"id\":\"adde472f-1eba-4571-af12-3169b8760ea0\",\"name\":\"Request\",\"type\":\"inputNode\",\"position\":{\"x\":110,\"y\":180}},{\"id\":\"ef22af77-1363-468c-920e-65afde643f45\",\"name\":\"计算\",\"type\":\"expressionNode\",\"content\":{\"expressions\":[{\"id\":\"xZmc0Nyjz7\",\"key\":\"totalAmount\",\"value\":\"goods.price
		// *
		// goods.quantity\"}]},\"position\":{\"x\":490,\"y\":170},\"description\":\"函数计算\"},{\"id\":\"1b68f4b6-4f38-4e89-b9d4-bc74a607dc03\",\"name\":\"Response\",\"type\":\"outputNode\",\"position\":{\"x\":910,\"y\":160}}]}";
		String decision = "{\"contentType\":\"application/vnd.gorules.decision\",\"edges\":[{\"id\":\"fd58db13-4154-4234-9e35-38c370777219\",\"type\":\"edge\",\"sourceId\":\"adde472f-1eba-4571-af12-3169b8760ea0\",\"targetId\":\"ef22af77-1363-468c-920e-65afde643f45\"},{\"id\":\"a1100bb0-268c-4ae5-a344-529a707ad438\",\"type\":\"edge\",\"sourceId\":\"ef22af77-1363-468c-920e-65afde643f45\",\"targetId\":\"1b68f4b6-4f38-4e89-b9d4-bc74a607dc03\"}],\"nodes\":[{\"id\":\"adde472f-1eba-4571-af12-3169b8760ea0\",\"name\":\"Request\",\"type\":\"inputNode\",\"position\":{\"x\":110,\"y\":180}},{\"id\":\"ef22af77-1363-468c-920e-65afde643f45\",\"name\":\"表达式\",\"type\":\"expressionNode\",\"content\":{\"expressions\":[{\"id\":\"xZmc0Nyjz7\",\"key\":\"data\",\"value\":\"goods.price * goods.quantity\"}]},\"position\":{\"x\":490,\"y\":170},\"description\":\"表达式执行\"},{\"id\":\"1b68f4b6-4f38-4e89-b9d4-bc74a607dc03\",\"name\":\"Response\",\"type\":\"outputNode\",\"position\":{\"x\":910,\"y\":160}}]}";
		String input = "{\"goods\":{\"price\": 1.2,\"quantity\": 2}}";
		EvaluateResult result = engine.evaluate(decision.getBytes(StandardCharsets.UTF_8), JsonMapper.parse(input));
		System.out.println(JsonMapper.toJSONString(result));
	}

	@Test
	void testFun() {
		String decision = "{\"contentType\":\"application/vnd.gorules.decision\",\"nodes\":[{\"id\":\"2328557f-58ff-487d-b5c8-a72e140697f0\",\"name\":\"Request\",\"type\":\"inputNode\",\"position\":{\"x\":140,\"y\":100}},{\"id\":\"afef8a1c-1bce-411a-bb02-fb5887d81030\",\"name\":\"JS 函数\",\"type\":\"functionNode\",\"content\":\"/**\\n* @param {import('gorules').Input} input\\n* @param {{\\n*  moment: import('dayjs')\\n*  dayjs: import('dayjs')\\n*  Big: import('big')\\n*  env: Record<string, any>\\n* }} helpers\\n*/\\nconst handler = (input, { moment, dayjs, Big, env }) => { \\n    console.log(input);\\n    const momentValid = typeof moment === 'function' && Object.keys(moment).includes('isDayjs');\\n    const dayjsValid = typeof dayjs === 'function' && Object.keys(moment).includes('isDayjs');\\n    const bigjsValid = typeof Big === 'function';\\n  return {\\n    momentValid,\\n    dayjsValid,\\n    bigjsValid,\\n    totalAmount: input.price * input.quantity,\\n    moment: moment().format('YYYY-MM-DD HH:mm:ss')\\n  };\\n}\",\"position\":{\"x\":450,\"y\":90},\"description\":\"方法\"},{\"id\":\"a821ccb4-3426-4745-8ba4-1cbfd6b4788a\",\"name\":\"Response\",\"type\":\"outputNode\",\"position\":{\"x\":770,\"y\":90}}],\"edges\":[{\"id\":\"7e5054a2-8933-4eed-b568-a7437ad382c2\",\"type\":\"edge\",\"sourceId\":\"2328557f-58ff-487d-b5c8-a72e140697f0\",\"targetId\":\"afef8a1c-1bce-411a-bb02-fb5887d81030\"},{\"id\":\"ca78c2d3-233c-4f8f-afbe-7066f485d07c\",\"type\":\"edge\",\"sourceId\":\"afef8a1c-1bce-411a-bb02-fb5887d81030\",\"targetId\":\"a821ccb4-3426-4745-8ba4-1cbfd6b4788a\"}]}";
		// String input = "{\"price\":1.2,\"quantity\":2}";
		String input = "{\"price\":1.2,\"quantity\":2}";
		EvaluateResult result = engine.evaluate(decision.getBytes(StandardCharsets.UTF_8), JsonMapper.parse(input));
		System.out.println(JsonMapper.toJSONString(result));
	}

	@Test
	void testRunFunction() {
		String handlerFunc = "const handler = (input, { moment, dayjs, Big, env }) => { \n"
				+ "    console.log(input);\n"
				+ "    const momentValid = typeof moment === \\\"function\\\" && Object.keys(moment).includes('isDayjs');\n"
				+ "    const dayjsValid = typeof dayjs === 'function' && Object.keys(moment).includes('isDayjs');\n"
				+ "    const bigjsValid = typeof Big === 'function';\n" + "  return {\n"
				+ "    totalAmount: input.price * input.quantity,\n" + "    momentValid,\n" + "    dayjsValid,\n"
				+ "    bigjsValid\n" + "  };\n" + "}";
		String input = "{\"price\":1.2,\"quantity\":2}";
		JsonNode result = engine.runFunction(handlerFunc, JsonMapper.parse(input));
		System.out.println(result);
	}

	@Test
	void testRunExpression() {
		String expression = "goods.price * goods.quantity";
		String input = "{\"goods\":{\"price\": 0.2,\"quantity\": 2}}";
		// String expressionTpl =
		// "{\"contentType\":\"application/vnd.gorules.decision\",\"edges\":[{\"id\":\"fd58db13-4154-4234-9e35-38c370777219\",\"type\":\"edge\",\"sourceId\":\"adde472f-1eba-4571-af12-3169b8760ea0\",\"targetId\":\"ef22af77-1363-468c-920e-65afde643f45\"},{\"id\":\"a1100bb0-268c-4ae5-a344-529a707ad438\",\"type\":\"edge\",\"sourceId\":\"ef22af77-1363-468c-920e-65afde643f45\",\"targetId\":\"1b68f4b6-4f38-4e89-b9d4-bc74a607dc03\"}],\"nodes\":[{\"id\":\"adde472f-1eba-4571-af12-3169b8760ea0\",\"name\":\"Request\",\"type\":\"inputNode\",\"position\":{\"x\":110,\"y\":180}},{\"id\":\"ef22af77-1363-468c-920e-65afde643f45\",\"name\":\"表达式\",\"type\":\"expressionNode\",\"content\":{\"expressions\":[{\"id\":\"xZmc0Nyjz7\",\"key\":\"data\",\"value\":\"%s\"}]},\"position\":{\"x\":490,\"y\":170},\"description\":\"表达式执行\"},{\"id\":\"1b68f4b6-4f38-4e89-b9d4-bc74a607dc03\",\"name\":\"Response\",\"type\":\"outputNode\",\"position\":{\"x\":910,\"y\":160}}]}";;
		// EvaluateResult evaluate = engine.evaluate(String.format(expressionTpl,
		// expression).getBytes(StandardCharsets.UTF_8), JsonMapper.parse(input));
		// System.out.println(evaluate);
		for (int i = 0; i < 10000; i++) {
			input = "{\"goods\":{\"price\": 0.2,\"quantity\": " + (i + 1) + "}}";
			JsonNode result = engine.runExprWithRule(expression, JsonMapper.parse(input));
			System.out.println(result);
		}

	}

}
